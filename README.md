# Race simulation

This race simulation was written in java and only uses the semaphores.

There are three obstacles: 
1. Forest: Each racer will search for a map in a forest. 
2. Mountain: There is only one path through the mountain (meaning only one racer can go through the path).
3. River: Only groups of N_racers can swim through the river.

All racers will rest before the race and in between obstacles.
The race sequence will be the following:
1. Rest
2. Forest
3. Rest
4. Mountain
5. Rest
6. River

At the end of the race, the judge will display the total time it took for the racer to finish the race.
Each racer will wait for its friend to finish the race and they will go home together. If the racer's friend has finished the race, the racer will go home alone.
