# Authentication system
System of delimitation of users' rights on top of password authentication.

###Version 2.0:
- Added protection against unauthorised copying and exploitation of the program that is enabled by the [AppProtectionService] (https://github.com/RomarioGorilyi/AppProtectionService). 
- Protection is provided by [Siganture.java] (https://github.com/RomarioGorilyi/AuthenticationSystem/tree/master/src/main/java/ua/ipt/signature) and [AuthenticationManager.java] (https://github.com/RomarioGorilyi/AuthenticationSystem/blob/master/src/main/java/ua/ipt/main/AuthenticationManager.java).

**Unregistered user** in the system can:

1. Enter the system
2. Get an About information of the system
3. Exit the app

**ADMIN** can:

1. Change his password
2. Show all users
3. Add new user
4. Block user
5. Get an About information of the system
6. Exit the app

Simple **User** can:

1. Change his password
2. Get an About information of the system
3. Exit the app


*Password restrictions*: 
  - only **Latin** and **Cyrillic** symbols
  - **at least 1** Latin and Cyrillic character **simultaniously**.
