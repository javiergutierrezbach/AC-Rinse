Javier Gutierrez Bach, Amr Al Dayeh, Elena Rawlinson
COSC 112 
Professor Alfeld 
May 9, 2023 


Read Me 

Downloads before you Compile 
● Pull the Project folder from the GitHub repository.
● Make sure that the direction Project/src contains:
○ All images 
○ ACRinse.txt 
○ json-simple-1.1.jar 
● Download JSON external libraries using this link (if pulled from repository this step should not be necessary): 
http://www.java2s.com/Code/Jar/j/Downloadjsonsimple11jar.htm 
To compile: 
Direct the terminal to the src folder in your Project folder. 
Then run this command: 
javac -cp json-simple-1.1.jar -d classes Project.java 
To run the program after compiling, run this command: 
java -cp json-simple-1.1.jar:. Project.java 


How to use the program: 
● For all of the buttons and interfaces of the app, the main way to work your way through it is by using the mouse click. 
● First up, the program will ask you to log in or register. To log in, the program only needs an email as an input, which you will write on the textbox in the panel, and then if the account is found, it will log in as the user with that email. Otherwise, you can create a new user and give it a name, last name and an email address which will create a new account and store it in the data. 
● The program opens on the home page and the user can select any of the following buttons: 
○ On the bottom: 
■ Your profile - you can see your information, bubbles, your current 
machine status and your all-time total loads and overtime (late) loads.
■ Washing Tips - to promote good washing behavior that benefits everyone (and the environment!) 
■ Home - returns to the home page 
■ Queue - explained below 
■ Leaderboard - shows the top 10 users with the most bubbles to 
incentivize good washing machine etiquette. Bubbles will change 
depending on how many loads you complete or how late you are to take your loads from the machines. If you complete a load in any machine, you will get 100 bubbles. If you pick it up more than 10 minutes late, you will 
lose 15 bubbles. An hour late → lose 100 bubbles. 3 hours late → lose 200 bubbles. A day late → lose 500 bubbles. 
○ Machine status: 
■ You can see the status of every washer and dryer. It will be red if 
occupied, with the name of the user occupying it and the time remaining. It will be yellow if it is pending, meaning the load is ready but hasnt been taken out of the machine. Finally, if it is green, then the machine is 
available. 
○ Put a load in: 
■ This page shows you the washers, and if there is a machine available and you do not have a load in already, then you can put a load in said 
machine. Once you have washed a load, a button will appear on the 
button that takes you to the drier page, which works similarly to the 
washer page. If a machine is pending but the owner doesn't take their load, then they will have a 10 minute grace period to get their clothes, and after that period, anyone can take their clothes off in the app (and 
hopefully real life too). 
○ Share feedback: 
■ Opens a link to a form to provide feedback for our application 
● Queue: The queue is a FIFO linked list we made that users can join in the queue page of the app. This works so that if you really want to wash and all the machines are taken, then you can join the queue.We have a queue for both the dryers and the washers. 
● All of the data regarding machine availability is determined by data we created (fake users, emails, load availability) which is written in a JSON file (the file you downloaded, called ACRinse.txt). Our program takes the information from the JSON file and updates the machine statuses/availability accordingly. This data is constantly updated with a server that runs simultaneously as many users, and the data gets sent and received by all of the users, so that you can see in your app what other users do. 
● To use the server: have a copy of ACRinse.txt in the src file and compile normally, if you want to connect to a server from a client thread, command f “localhost” in the project file and then add the IP address of the computer the server is running on and make sure you have the same number as port for the server and client (should be 6066). You also need to uncomment all the parts of the code that are commented (in the main method, writeJson method, and the while(true) loop in runner). You can find "uncomment" above each snippet that needs to be uncommented.
● We ran into trouble when running our organized code across multiple .java files using the terminal so we submitted one Project.java file. If you want to see the code organized across multiple files please see the OrganizedCode folder.

To see more information on how the program works and general functionality, see the Project Specification document.