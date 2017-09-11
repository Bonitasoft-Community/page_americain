The Americain custom page can upload any organization, from a CSV source file.
It can run 
* to load a file at one moment, 
* to monitor a directory and load any file in this directory
* to run as a service

<img src="screenshot_americain.jpg" />

The CSV control different operations (see the CVS example in the test repository). The operation is control by the first colum.
1. Define the different stucture
The CSV can manage different type of record. The definition is done with the HEADER order:

HEADER;ROLE;RoleName;RoleDescription;RoleDisplayName;RoleIconName;RoleIconPath
HEADER;USER;UserName;UserPassword;UserEnable;UserFirstName;UserLastName;UserIconName;UserIconPath;UserTitle;UserJobTitle;UserManagerUserName;PROContactAddress;PROContactBuilding;PROContactCity;PROContactCountry;PROContactEmail;PROContactFaxNumber;PROContactMobileNumber;PROContactPhoneNumber;PROContactRoom;PROContactState;PROContactWebsite;PROContactZipCode;PERContactAddress;PERContactBuilding;PERContactCity;PERContactCountry;PERContactEmail;PERContactFaxNumber;PERContactMobileNumber;PERContactPhoneNumber;PERContactRoom;PERContactState;PERContactWebsite;PERContactZipCode
HEADER;GROUP;GroupName;GroupDescription;GroupDisplayName;GroupIconName;GroupIconPath;GroupParentPath
HEADER;MEMBERSHIP;UserName;RoleName;GroupPath
HEADER;PROFILE;ProfileName;ProfileDescription
HEADER;PROFILEMEMBER;ProfileName;UserName;RoleName;GroupPath


So, if the first column contains ROLE, the different attributes is then RoleName, RoleDescription, RoleDisplayName, RoleIconeName and RoleIconPath.
And for a ROLE, here a definition
ROLE;TSRoleBook;The role to see a book;Role book;Book.gif;c:/tmp/bob

or for a Membership:
MEMBERSHIP;Francis.Huster;TSRoleBook;/Bonitasoft/Consultant/Consultant FR

2. ROLE
Different role can be declare

3. USER
Define the user and all attributes Professional or Personnal.

4. GROUP
Define the group, and a Hierarchy of group can be declared

5. MEMBERSHIP
A membership is a link between a ROLE, a GROUP and ROLE. Key are based on UserName, GroupName and RoleName

6. PROFILE 
Create the different profile. Nota: a profile contains user and menu definition :the menu definition can't be manage here, only the user part

7. PROFILE MEMBER
Define the profile member, based on a User, a Role, a group.


How to integrate your own source ? 
By default the page accept a CVS format and a JSON format.

You can define a new Source (XML, LDAP, Your system) : see OrganizationIntSource.java. Your new source must implement this class. Then, you have a method 
public boolean isManageTheFile(final String fileName) 
If you return TRUE for a filename, your class are in charge to load it.




