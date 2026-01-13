# My Personal Project: Gym Progress Tracker

## What My Application Will Do
I'm creating a application that will track gym progress. It will be used to track weight progression throughout different excersizes selected. There will be basic excersizes for selection, and there will be option for the user to input any custom excersize. Based on the progression, there will be an option to customize a workout I personally design based on which muscles are progressing not as much. An extension I am thinking to add a macro-nutrient and calorie tracker with a sleep tracker. This would cover all bases of seeing growth in the gym. 

## Who Will Be Using My Application
This my goal for this application is to reach **everyone** going to the gym. This is targeted to anyone seeking progress in the gym, so more geared towards younger people focusing on building up strength and muscle. 

## Why This Project Is Of Interest To Me
There has been a growing popularity for going to the gym in the recent years. Many people are trying to get started but demotivated due to inconsistency and not seeing *growth*. I have been in sports for most of my life and been going to the gym for 3 years now. A lot of my friends often are losing out on progress due to simply not keep track of what they did the previous session and not feeling the motivation to push on the next. I personally would also sometimes find it difficult to remember my previous workout stats which negatively impacts my workouts causing ineffciency. There are lots of online gym trackers but many cost money, so I wanted to make this also largely motivated by my *own* interest.

## User Stories
- As a user, I want to be able to add an excersize to list of excersizes I have done after my previous gym session
- As a user, I want to be able to enter and record how many sets and reps of what weight I did for each excersize 
- As a user, I want to be able to view the stats on an excersize from a previous gym session
- As a user, I want to be able to view how much progress I have made every week, month, and year
- As a user, I want to see which excersize and corresponding muscle group I have made the least progress on. 
- As a user, when I select the quit option from the application menu, I want to be able to save my entire workout history with the sessions I just recorded added on to file if i wisht to do so and reminded to 
- As a user, when I start the application, I want to have the option to retrieve able to retrieve all the workouts I have added, and see the progress that I have made if I wish to or start a new workout history
- As a user, I want to be able to load and save the state of the application, and also prompted witht he option to save data before closing application

## Instructions for End User
- You can view the panel that displays the exercises that have already been added to the workout sessions by looking at the left panel titled "Workout Sessions" which shows all the recorded sessions
- You can generate the first required action related to the user story "adding multiple excercises to a workout session" by clicking "Workout" and then slecting "Add Workout Session" from the menu bar
- You can generate the second required action related to the user story "adding multiple exercises to a workout session" by clicking "Workout" → "View Excercise Progress" from the menu bar
- You can locate my visual component by looking at the bottom panel labeled "Exercise Progress Chart" which displays a line chart showing weight progression over time for selected exercises
- You can save the state of my application by clicking "File" and then "Save" from the menu bar as well as selecting "Save" when prompted to before closing the window
- You can reload the state of my application by clicking "File"and selecting "Load" from the menu bar

## Phase 4: Task 2
EVENT LOG
Fri Nov 28 00:13:22 PST 2025
Added exercise: pullup to Back session on 2025-11-11
---
Fri Nov 28 00:13:22 PST 2025
Added workout session: Back on 2025-11-11
---
Fri Nov 28 00:13:22 PST 2025
Added exercise: pullup to Back session on 2025-11-21
---
Fri Nov 28 00:13:22 PST 2025
Added workout session: Back on 2025-11-21
---
Fri Nov 28 00:13:59 PST 2025
Added exercise: pullup to Back session on 2025-11-28
---
Fri Nov 28 00:14:16 PST 2025
Added exercise: rows to Back session on 2025-11-28
---
Fri Nov 28 00:14:18 PST 2025
Added workout session: Back on 2025-11-28
---
END OF EVENT LOG

## Phase 4: Task 3
From the UML class diagram, and then looking into my classes, I think a big improvement that can come from refactoring is the redistribution of responsibilities in the larger classes. My GymTrackerGUI class has almost 500 lines, as it deals with UI layout creation, event handling, and dialog management. The GymTracker class also deals with both the central workout data structure and the progress calculation logic. These are things I would refactor. Within the GymTrackerGUI class, I think I could've used the single responsibility rule better. I could move the dialog creation component from GymTrackerGUI into separate dedicated dialog classes like AddSessionDialog, AddExerciseDialog for handling user input. The progress calculation methods in GymTracker could be moved to a whole separate ProgressCalculator class, this would make the GymTracker class much less loaded and more focusd. I think it also could be good to add a SessionManager class to handle the workout session operations currently in GymTracker, also to make it more focused on storage and less on the complex operations.
These changes I could make would make a more clear seperation of focuses in classes,improving the overall structure. This would increase the number of classes, and more complex to include in the UML diagram, which might be the tradeoff; however, each would have a single, well-defined purpose. The benefit would be much improved code organization and easier future changes and additions, for exmaple features like different progress metrics or export functionality were to be added later.

