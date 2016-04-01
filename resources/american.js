'use strict';
/**
 * 
 */

(function() {

	if (typeof String.prototype.startsWith != 'function') {
		// see below for better implementation!
		String.prototype.startsWith = function (str){
			return this.indexOf(str) == 0;
		};
	}

	var appCommand = angular.module('american', ['ngAnimate', 'ui.bootstrap', 'timer', 'toaster', 'angularFileUpload']);

//	Constant used to specify resource base path (facilitates integration into a Bonita custom page)
	appCommand.constant('RESOURCE_PATH', 'pageResource?page=custompage_american&location=');

//	User app list controller
	appCommand.controller('DigesterController', function($rootScope, $scope, $upload, $http, toaster) {
		var me = this;
		$scope.dropzonenewvalue = "";
		$scope.archivezonenewvalue = "";

		$scope.optoperationgroups="BOTH"
		$scope.optoperationroles="BOTH"
		$scope.optoperationusers="BOTH"
		$scope.optoperationprofiles="BOTH";
		$scope.optoperationprofilemembers="BOTH";

		$scope.optpurgegroups=false;
		$scope.optpurgegroles=false;
		$scope.optpurgeusers=false;
		$scope.optpurgeprofiles=false;
		$scope.optregisternewuserinprofileuser="ALWAYSUSERPROFILE";
		
		$scope.currentUploadFileIndex = 0;
		$scope.refreshisrunning = false;
		
		$rootScope.history = [];

		this.propop = function() {
			toaster.pop('success', "Properties have been set", "");
		};
		
		this.prodropzonepop = function() {
			toaster.pop('success', "Drop zone property has been set", "");
		};
		
		this.proarchivezonepop = function() {
			toaster.pop('success', "Archive zone property has been set", "");
		};

		this.proloadpop = function() {
			toaster.pop('success', "Properties have been loaded", "");
		};
		
		this.sameloadpop = function() {
			toaster.pop('error', "The drop zone and archive zone properties cannot be the same directory", "");
		};

		this.emptyrefreshpop = function() {
			toaster.pop('warning', "No archives have been digested", "");
		};

		this.createrefreshpop = function() {
			toaster.pop('success', "The archive directory has been generated", "");
		};
		
		this.fullrefreshpop = function() {
			toaster.pop('success', "Archive(s) has been digested", "");
		};

		this.errorpop = function() {
			toaster.pop('error', "An occurred error", "");
		};
		
		this.dropzonepropertyerrorpop = function() {
			toaster.pop('error', "The path of the drop zone is bad", "");
		};
		
		this.archivezonepropertyerrorpop = function() {
			toaster.pop('error', "The path of the archive zone is bad", "");
		};
		
		this.uploadsuccesspop = function() {
			toaster.pop('success', "A BAR file has been uploaded", "");
		};

		this.uploadwarningpop = function() {
			toaster.pop('warning', "A file has been avoided due to error", "");
		};
		
		this.uploaderrorpop = function() {
			toaster.pop('error', "An error occurred uploading a file in the archive directory", "");
		};
		
		this.flushToasts = function() {
			toaster.flush();
		};
		
			
		this.refreshfrombtn = function() {
			if(!$scope.refreshisrunning) {
				$scope.refreshisrunning = true;
			
				//flush the toasts
				me.flushToasts();
				// contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
				// $scope.$apply();
				
				
				// $http.get( '?page=custompage_american&action=refresh' )
				//	.success( function (result) {
					
				
				$.ajax({
					method : 'GET',
					url : '?page=custompage_american&action=refresh',			
					contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
					success : function (result) {
						var resultArray = JSON.parse(result);
						var arrayLength = resultArray.length;
						for (var i = 0; i < arrayLength; i++) {
							$rootScope.history.unshift(resultArray[i]);
						}
						if(arrayLength == 0) {
							me.emptyrefreshpop();
						} else if(resultArray[0].name.startsWith("Create Archive folder")) {
							me.createrefreshpop();
							if(arrayLength > 1) {
								me.fullrefreshpop();
							}
						} else {
							me.fullrefreshpop();
						}
						$scope.refreshisrunning = false;
						$scope.$apply();
					},
					error: function (result) {
						me.errorpop();
						$scope.refreshisrunning = false;
						$scope.$apply();
					},
					complete: function () {
						$scope.refreshisrunning = false;
					}
				});
				
			}
		};

		this.autorefresh = function() {
			if(!$scope.refreshisrunning) {
				$scope.refreshisrunning = true;
				$.ajax({
					method : 'GET',
					url : '?page=custompage_american&action=refresh',			
					contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
					success : function (result) {
						var resultArray = JSON.parse(result)
						var arrayLength = resultArray.length;
						for (var i = 0; i < arrayLength; i++) {
							$rootScope.history.unshift(resultArray[i]);
						}
						if(arrayLength != 0) {
							if(resultArray[0].name.startsWith("Create Archive folder")) {
								me.createrefreshpop();
								if(arrayLength > 1) {
									me.fullrefreshpop();
								}
							} else {
								me.fullrefreshpop();
							}
						}

						// $scope.$apply();
					},
					error: function (result) {
						me.errorpop();
						// $scope.$apply();
					},
					complete: function () {
						document.getElementsByTagName('timer')[0].addCDSeconds(60);
						$scope.refreshisrunning = false;
					}
				});
			} else {
				document.getElementsByTagName('timer')[0].addCDSeconds(60);
			}
		};

		this.getproperties = function(toast) {
			$.ajax({
				method : 'GET',
				url : '?page=custompage_american&action=getproperties',			
				contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
				success : function (result) {
					var resultArray = JSON.parse(result);
					$scope.dropzonenewvalue 		= resultArray.dropzone;
					$scope.archivezonenewvalue 		= resultArray.archivezone;
					$scope.optoperationgroups		= (resultArray.optoperationgroups==null ? "BOTH": resultArray.optoperationgroups);
					$scope.optoperationroles		= (resultArray.optoperationroles==null ? "BOTH": resultArray.optoperationroles);
					$scope.optoperationusers		= (resultArray.optoperationusers==null ? "BOTH": resultArray.optoperationusers);
					$scope.optoperationprofiles		= (resultArray.optoperationprofiles==null ? "BOTH": resultArray.optoperationprofiles);
					$scope.optoperationprofilemembers		= (resultArray.optoperationprofilemembers==null ? "BOTH": resultArray.optoperationprofilemembers);
					// optoperationmemberships
					$scope.optpurgegroups			= (resultArray.optpurgegroups== "true" ? true : false);
					$scope.optpurgeroles			= (resultArray.optpurgeroles=="true" ? true : false);
					$scope.optpurgeusers			= (resultArray.optpurgeusers =="true" ? true :false);
					$scope.optregisternewuserinprofileuser = (resultArray.optregisternewuserinprofileuser==null ? "USERPROFILEIFNOTREGISTER": resultArray.optregisternewuserinprofileuser);
					
					$scope.optpurgeprofiles			= (resultArray.optpurgeprofiles =="true" ? true :false);
		
					if(toast) {
						me.proloadpop();
					}
					// $scope.$apply();
				},
				error: function (result) {
					me.errorpop();
					// $scope.$apply();
				}
			});
		};

		this.setproperties = function() {
			//flush the toasts
			me.flushToasts();
			// $scope.$apply();
			var url = '?page=custompage_american&action=setproperties&dropzone=' + $scope.dropzonenewvalue + '&archivezone=' + $scope.archivezonenewvalue;
				url = url + "&optoperationgroups="+ $scope.optoperationgroups;
				url = url + "&optoperationroles=" + $scope.optoperationroles;
				url = url + "&optoperationusers=" + $scope.optoperationusers;
				url = url + "&optoperationprofiles=" + $scope.optoperationprofiles;
				url = url + "&optoperationprofilememberss=" + $scope.optoperationprofilemembers;
				url = url + "&optpurgegroups=" + $scope.optpurgegroups;
				url = url + "&optpurgeroles=" + $scope.optpurgeroles;
				url = url + "&optpurgeusers=" +	$scope.optpurgeusers;
				url = url + "&optpurgeprofiles=" +	$scope.optpurgeprofiles;
				url = url + "&optregisternewuserinprofileuser=" + $scope.optregisternewuserinprofileuser;
				

			$.ajax({
				method : 'GET',
				url : url,			
				contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
				success : function (result) {
					if(result != "[{}]") {
						var dropzoneerror = false;
						var archivezoneerror = false;
						var resultArray = JSON.parse(result);

						$rootScope.history.unshift( resultArray.history );
						if (resultArray.errordropzone != "")
						{
							dropzoneerror = true;
							me.dropzonepropertyerrorpop();
						}
						if (resultArray.errorarchivedropzone != "")
						{
							archivezoneerror = true;
							me.archivezonepropertyerrorpop();
						}
						// $scope.$apply();

						if(!dropzoneerror || !archivezoneerror) {
							if(!dropzoneerror) {
								me.prodropzonepop();
								$scope.$apply();
							}
							if(!archivezoneerror) {
								me.proarchivezonepop();
								$scope.$apply();
							}
						} else {
							me.propop();
							// $scope.$apply();
						}
					} else {
						me.sameloadpop();
						// $scope.$apply();
					}
					//me.getproperties(false);
				},
				error: function (result) {
					me.errorpop();
					// $scope.$apply();
				}
			});
		};

		//$scope.$on('timer-tick', function (event, args) {
			//me.autorefresh();
		//});
		
		$scope.$watch('files', function() {
			$scope.currentUploadFileIndex = 0;
			for (var i = 0; i < $scope.files.length; i++) {
				var file = $scope.files[i];
				$scope.upload = $upload.upload({
					url: 'fileUpload',
					method: 'POST',
					data: {myObj: $scope.myModelObj},
					file: file
				}).progress(function(evt) {
//					console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% file :'+ evt.config.file.name);
				}).success(function(data, status, headers, config) {
//					console.log('file ' + config.file.name + 'is uploaded successfully. Response: ' + data);
					var url='?page=custompage_american&action=uploadbar&file=' + data;
					url = url + '&name=' + $scope.files[$scope.currentUploadFileIndex].name;
					$scope.currentUploadFileIndex = $scope.currentUploadFileIndex + 1;
					$.ajax({
						method : 'GET',
						url : url,
						contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
						success : function (result) {
							var resultArray = JSON.parse(result)
							var arrayLength = resultArray.length;
							for (var i = 0; i < arrayLength; i++) {
								$rootScope.history.unshift(resultArray[i]);
								if(resultArray[i].status.toString().startsWith("Error: ")) {
									me.uploadwarningpop();
								} else {
									me.uploadsuccesspop();
								}
							}
							// $scope.$apply();
						},
						error: function ( result ) {
							var resultArray = JSON.parse(result)
							var arrayLength = resultArray.length;
							for (var i = 0; i < arrayLength; i++) {
								$rootScope.history.unshift(resultArray[i]);
							}
							me.uploaderrorpop();
							// $scope.$apply();
						}
					});
				});
			}
		});
	});
	
	
	   
// --------------------------------------------------------------------------
//
// Controler MainControler
//
// --------------------------------------------------------------------------
	
appCommand.controller('MainController', 
	function () {
	
	this.isshowhistory = false;
	
	this.showhistory = function( show )
	{
	   this.isshowhistory = show;
	}

	
		
});

	   
// --------------------------------------------------------------------------
//
// Controler MainControler
//
// --------------------------------------------------------------------------
	
appCommand.controller('ExamplesController', 
	function () {
	
	this.isshowexample = false;
	this.showexample = function( show )
	{
	   this.isshowexample = show;
	}

	
		
});


})();

/*
	*/
