# Entity-Component-System pattern

Two types of the systems:
1. One that is creating and defining Entity behavior
2. Another that treats Entities based only on the certain set of features or in this case defined Components. 
E.g. Physics system recalculates the position and rotation of all components (`Transform` component) that has active `Motion` and doesn't touch Entities if they don't have `Motion`