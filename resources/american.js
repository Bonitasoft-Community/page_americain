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
		
<<<<<<< HEAD
		this.currentUploadFileIndex = 0;
=======
		$scope.currentUploadFileIndex = 0;
>>>>>>> 3ddeeea1b4aec128a3935b2368517b1e6d60cb3d
		this.refreshisrunning = false;
		
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
			toaster.pop('success', "A file has been uploaded", "");
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
			console.log("refreshfrombtn : already in progress?  "+this.refreshisrunning);
			
			if(!this.refreshisrunning) {
				this.refreshisrunning = true;
			
				//flush the toasts
				me.flushToasts();
				var self=this;
				self.wait=true;
				// contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
				// $scope.$apply();
				
				
						
				console.log("refreshfrombtn : PLAY IT");

<<<<<<< HEAD
				var url='?page=custompage_american&action=refresh';
				
				$http.get( url )
					.success( function (result) {
						self.wait=false;
						var actionsList = result.actions;
						var arrayLength = actionsList.length;
=======
				$.ajax({
					method : 'GET',
					url : '?page=custompage_american&action=refresh',			
					contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
					success : function (result) {
						self.wait=false;
						var resultArray = JSON.parse(result);
						var arrayLength = resultArray.length;
>>>>>>> 3ddeeea1b4aec128a3935b2368517b1e6d60cb3d
						for (var i = 0; i < arrayLength; i++) {
							$rootScope.history.unshift(actionsList[i]);
						}
						if(arrayLength == 0) {
							me.emptyrefreshpop();
						} else if(actionsList[0].name.startsWith("Create Archive folder")) {
							me.createrefreshpop();
							if(arrayLength > 1) {
								me.fullrefreshpop();
							}
						} else {
							me.fullrefreshpop();
						}
						me.refreshisrunning = false;
						$scope.$apply();
<<<<<<< HEAD
					})
					.error( function (result) {
=======
					},
					error: function (result) {
>>>>>>> 3ddeeea1b4aec128a3935b2368517b1e6d60cb3d
						self.wait=false;
						me.errorpop();
						me.refreshisrunning = false;
						$scope.$apply();
<<<<<<< HEAD
					});
=======
					},
					complete: function () {
						self.wait=false;
						me.refreshisrunning = false;
					}
				});
				
>>>>>>> 3ddeeea1b4aec128a3935b2368517b1e6d60cb3d
			}
		};

		this.clickautorefresh = function()
		{

			console.log("clickautorefresh : now it's "+this.options.autorefresh);

			if (this.options.autorefresh)
			{
				// REarm the timer
				console.log("clickautorefresh : ARM Timer");
				document.getElementsByTagName('timer')[0].start();
			}
			else
			{
				console.log("clickautorefresh : STOP Timer");
				document.getElementsByTagName('timer')[0].stop();
				
			}
		}
		this.autorefresh = function() {
			
			console.log("autorefresh : "+this.options.autorefresh);
			if (this.options.autorefresh===false)
			{
				console.log("autorefresh : No auto refresh");
				 return;
			}
			console.log("autorefresh : PLAY IT");

			
			if(!this.refreshisrunning) {
				this.refreshisrunning = true;
				var me=this;
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
						// REarm the timer
						document.getElementsByTagName('timer')[0].addCDSeconds(60);
						me.refreshisrunning = false;
					}
				});
			} else {
				document.getElementsByTagName('timer')[0].addCDSeconds(60);
			}
		};
		
		/* Properties */
		var options={ "dropzone":"", "archivezone":"","groups":"BOTH","roles":"BOTH","users":"BOTH","profiles":"BOTH",
				"profilemembers":"BOTH",
				"purgegroups":false,
				"purgegroles":false,
				"purgeusers":false,
				"purgeprofiles":false,
				"registernewuserinprofileuser":"ALWAYSUSERPROFILE",
				"autorefresh":true};
		

		this.getproperties = function(toast) {
			var self=this;
			self.wait=true;
			$http.get( '?page=custompage_american&action=getproperties' )
					.success( function ( result ) {
						self.wait=false;
						
						self.options= result;
						if (!self.options)
							self.options={};
						if (typeof self.options.autorefresh === "undefined")
							self.options.autorefresh=true;
						self.options.autorefresh 	= self.stringToBoolean( self.options.autorefresh);
						self.options.purgegroups 	= self.stringToBoolean( self.options.purgegroups);
						self.options.purgeroles 	= self.stringToBoolean( self.options.purgeroles);
						self.options.purgeusers 	= self.stringToBoolean( self.options.purgeusers);
						self.options.purgeprofiles 	= self.stringToBoolean( self.options.purgeprofiles);
								/*
								==="true")
							self.options.autorefresh=true;
						else
							self.options.autorefresh=false;
						if (self.options.purgegroups ==="true")
							self.options.purgegroups=true;
						else
							self.options.purgegroups=false;
						*/
							
						if(toast) {
							me.proloadpop();
						}
					})
					.error( function ( jsonResult ) {
						self.wait=false;
						me.errorpop();
						});
			
			
			
		};

		this.stringToBoolean = function( valueSt)
		{
			if (valueSt ==="true")
				return true;
			return false;
		}

		this.setproperties = function() {
			var self=this;
			self.wait=true;

			//flush the toasts
			me.flushToasts();
			
			
			var json = encodeURI( angular.toJson( this.options, false));
			
			$http.get( '?page=custompage_american&action=setproperties&paramjson='+json )
				.success( function ( jsonResult ) {
					self.wait=false;
					
						var dropzoneerror = false;
						var archivezoneerror = false;
						var resultArray = jsonResult;

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
					
					//me.getproperties(false);
				})
				.error( function ( jsonResult ) {
					self.wait=false;
					me.errorpop();
				});
		};

		
		var meForUpload = this;
		this.savetoaster= toaster;
		$scope.$watch('files', function() {
			meForUpload.currentUploadFileIndex = 0;
			meForUpload.loadmessage="";
			for (var i = 0; i < $scope.files.length; i++) {
				var file = $scope.files[i];
				meForUpload.loadmessage="Upload ["+file.name+"] ...";
				meForUpload.savetoaster.pop('success', "Start upload ["+file.name+"]", "");
				this.upload = $upload.upload({
					url: 'fileUpload',
					method: 'POST',
					data: {myObj: this.myModelObj},
					file: file
				}).progress(function(evt) {
//					console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% file :'+ evt.config.file.name);
				}).success(function(data, status, headers, config) {
					console.log('file ' + config.file.name + 'is uploaded successfully. Response: ' + data);
					var url='?page=custompage_american&action=uploadbar&file=' + data;
					url = url + '&name=' + $scope.files[meForUpload.currentUploadFileIndex].name;
					meForUpload.currentUploadFileIndex = meForUpload.currentUploadFileIndex + 1;
					$.ajax({
						method : 'GET',
						url : url,
						contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
						success : function (result) {
							console.log('file ' + config.file.name + 'is given to american monitor. Response: ' + result);
							var resultArray = JSON.parse(result)
							var arrayLength = resultArray.length;
							console.log('arrayLength= ' + arrayLength );
							for (var i = 0; i < arrayLength; i++) {
								$rootScope.history.unshift(resultArray[i]);
								if(resultArray[i].status.toString().startsWith("Error: ")) {
									meForUpload.loadmessage="File ["+config.file.name+"] loaded with error";
									meForUpload.savetoaster.pop('warning', "the file ["+config.file.name+"] has been avoided due to error", "");
								} else {
									meForUpload.loadmessage="File ["+config.file.name+"] loaded";
									// toaster.pop('success', "the file ["+config.file.name+"] has been uploaded_2", "");
									meForUpload.savetoaster.pop('success', "the file ["+config.file.name+"] has been uploaded", "");
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
							meForUpload.loadmessage="File ["+config.file.name+"] loaded with error";
							meForUpload.savetoaster.pop('error', "An error occurred uploading a file ["+config.file.name+"] in the monitor directory", "");
							// $scope.$apply();
						}
					});
				}).error(function() {} );
			}
		});
		/*
		$scope.$watch('files', function() {
			$scope.currentUploadFileIndex = 0;
			var me=this;
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
				}).error(function() {} );
			}
		});
		*/
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
