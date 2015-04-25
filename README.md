## birds

Example implementation of Craig Reynolds' [Boids automata algorithm](http://www.red3d.com/cwr/boids/) in
Clojure using the Quil library as an interface to Processing.

### Rules for Boids:

1. Birds attempt to avoid collisions with flockmates by steering away
   from neighbors average position when too close.
2. Birds attempt to follow a shared trajectory by steering toward the
   average heading of their neighbors.
3. Birds attempt to "flock" together by steering toward the average
   position of their neighbors.

Other topics to explore:

* Obstacle avoidance?
* Goal-seeking? (steer toward mouse or some other defined objective?)

#### Issues

* Rotation -- current heading rotation implementation has issues with
  turning in the optimal direction (i.e. turning right 30 degrees
  instead of left 330 degrees to reach the same heading). This produces
  some jumpy behavior sometimes.

#### sample

A sample video of what the algorithm looks like so far can be seenhere:
[https://vimeo.com/126003095](https://vimeo.com/126003095).

### Usage

Should be able to run from the project root with

```
lein deps
lein run
```

