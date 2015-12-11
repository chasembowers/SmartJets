SmartJets
========

A 2D projectile game where opponents mimick user strategies with increasing skill via machine learning

![alt tag](https://raw.githubusercontent.com/chasembowers/SmartJets/master/gameplay.gif)

## Description

The project is in its beginning stages.  Several jets occupy a circular arena.  The jets can move through
the arena and fire projectiles in any direction at a regular interval.  The user controls one (blue) jet and attempts
to hit enemy (red) jets with its projectiles while dodging their projectiles. More importantly, with each consecutive
round of playing, the enemy jets observe and attempt to reproduce the strategy of the user's jet via machine learning.
With each new round, the enemy jets have access to more data and consequently become more skilled.

##How To Play

This project does not yet have an executable. Run the Main class to test the game.

## Machine Learning Implementation

A set of features is generated to describe every game state for the purpose of machine learning.
During feature generation, the jet's position is described only by its distance from
the origin, and the positions of enemy projectiles are described radially, in relation to jet's perspective of the origin. 
This allows any game state to be generalized to all rotations of that game state
about the origin and greatly accelerates the learning process. The features presently generated are the relative polar 
coordinates of the three closest projectiles and the jet's distance from the origin.

Furthermore, a jet's movement is also described relative to its perspective of the origin to remain consistent
with feature generation. For example, a clockwise movement anywhere in the arena is labeled identically. These
game states (jet/projectile positions) and corresponding user actions are stored and used to classify future game states 
encountered by enemy jets into actions.

Currently, an unoptimized Random Forest with a maximum capacity of 10,000 training samples is used to control enemy jets. 

## External Packages

Weka - Data Mining and Machine Learning for Java

Copyright 2015 Chase Bowers

This program is distributed under the terms of the GNU General Public License (LICENSE.txt).
