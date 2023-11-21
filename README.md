# BankApp
Sometimes, budgeting can be difficult. With ever-increasing inflation, it is more important
than ever to be tracking one's monthly expenses. We wanted to make an application that would keep
track of your daily spending habits in an easy to view manner. After registering your account, 
you can create a banking wallet (i.e., 'Savings Account,' 'Checking Account,' etc). Within this
wallet, you can add a transaction with the press of a button! You can even transfer money between
different wallets. Finally, you can delete transactions (say, you logged a transaction 
unintentionally). You can even delete an entire wallet. And if you want to change your username
or password, it's easy to do so! You are going to love our BankApp! We make tracking finances
easy!

## Application Setup
### Install Your Database
1. Install MySQL on your computer if you haven't already.
```
https://dev.mysql.com/downloads/mysql/
```
Select the proper download file for your operating system using the dropdown menu. For MacOS
Silicon, make sure to select the 'ARM' download. For Intel-based Macs, make sure to select (x84,
64-bit).

2. Download Java 17 (Long Term Support) on your computer if you haven't already.
```
MacOS Download: https://www.oracle.com/java/technologies/downloads/#jdk17-mac
Linux Download: https://www.oracle.com/java/technologies/downloads/#jdk17-linux
Windows Download: https://www.oracle.com/java/technologies/downloads/#jdk17-windows
```

3. Clone our GitHub Repository onto your machine.
```
https://github.com/jforseth210/CS-389-Semester-Project.git
```

4. Download the IDE of your choice if you don't have one installed already (optional if running in terminal).
```
# Eclipse (Free)
Windows: https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/2023-09/R/eclipse-java-2023-09-R-win32-x86_64.zip
MacOS (Silicon): https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/2023-09/R/eclipse-java-2023-09-R-macosx-cocoa-aarch64.dmg
MacOS (Intel): https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/2023-09/R/eclipse-java-2023-09-R-macosx-cocoa-x86_64.dmg
Linux (x64): https://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/2023-09/R/eclipse-java-2023-09-R-linux-gtk-x86_64.tar.gz

# Visual Studio Code (Free)
Any Operating System: https://code.visualstudio.com/download

# IntelliJ IDEA (Free with College Account)
Windows: https://www.jetbrains.com/idea/download/?section=windows
MacOS: https://www.jetbrains.com/idea/download/?section=mac
Linux: https://www.jetbrains.com/idea/download/?section=linux
```

### Run the Application
Now that you have everything installed, you are ready to run our program. 
1. Navigate to the root directory where you stored your project.
2. Run the program through the terminal using the following command:
```
./gradlew bootrun
```
3. Navigate to the browser of your choice and open the following website:
```
http://localhost:8080/
```

4. Enjoy our application! You are all set to start tracking your finances like a pro!

## Application Features
### Account Creation
We want to make using our application as easy as possible. To use our banking application, you
simply need to register an account with us, and you are ready to go! To register, we'll ask for a
couple pieces of information:
* First and Last Name: We want to know what to call you!
* Email: We get your email, but never send any spam emails or sell this information to others
* Username: You get to be creative here! Have fun making a username to identify yourself with
* Password: Everyone needs a password to log into an account! Pick on you'll be able to remember

Things we will _never_ ask for:
* Social Security Number: That's a bit too much of information for us! We want this to remain private
* Birthday: We just don't need to know your birthday! Feel safe knowing that we won't be asking for much
personally identifiable information

Once you have successfully created an account, browse our application! You'll notice there's a 
dropdown menu in the top right-hand corner. In this menu, you can change your username, change
your password, or log out of our application!

### Transaction Details
We want to make logging a transaction simple. At the bottom of the screen, you will see a 'log 
transaction' button. Upon clicking this button, you will be able to input the following:
* Transaction Name: Put a description here of what you bought or received! The key is naming
your transaction something you will understand and remember if you look at it a year into the future
* Transaction Amount: How much did you spend or receive? Put that here!
* From Where?: Where was this money spent? Let's say you bought a drink at Starbucks. Then put
'Starbucks' here!

At the bottom of the transaction logger, you'll see an 'income' or 'expense' button. If you _spent_
the money, click the 'expense' button. If you _received_ the money, hit the 'income' button.

### Deleting Transaction Details
Oops! You accidentally logged a transaction you didn't really make. Deleting is simple! Next to
each transaction is a little trash can. If you press the trash can, the transaction will be
deleted. **Note that the trash can will not prompt you to confirm deletion. Once you click
the trash can, that transaction is _gone forever._** However, if you delete something you shouldn't
have, it's easy to just re-log the transaction using the transaction button detailed above.

### Transfer Details
Let's say you just moved some money from your savings wallet to your checkings wallet. You can
log this change by using our 'transfer' button! The transfer button (located at the bottom of 
the screen) transfers money _from_ the wallet that you are in _to_ the wallet that you specify.
You can then input the amount of money you wish to transfer, and our application will log this
information for you! You can easily see exactly where your money is going!

### Making New Wallet Details
You can have tons of wallets in our application. There are no shortages of budgeting atmospheres
available to you! If you want to add a wallet, simply navigate to the 'add' button (at the top-left
of our application). You will be prompted to name the account (i.e., 'Checking,' 'Savings,' etc).
Then, just declare the starting balance for your wallet and you are all set!

### Deleting Wallet Details
Oops! You made a wallet that doesn't exist anymore. It's simple to delete an entire wallet. 
**Once a wallet is deleted, it is _gone forever!_** To delete a wallet, simply navigate to the
'delete [your wallet name]' button. You will be prompted to confirm deletion. Once you hit
confirm, the entire wallet will be deleted!

## Developer Guide
### Running our Unit Tests
1. Upon up the Terminal Command Prompt within your IDE.
2. Type in ```./gradlew test```
3. All of the tests created for our program will run and display whether they are successful
or not.

### Accessing Documentation
1. Navigate to the 'build' folder.
2. Within the 'build' folder, navigate to 'docs.'
3. Find the 'index.html' file. Open this file in the browser of your choice and browse through
our javadoc for our classes, interfaces, and methods!