# ArcadeDB Sequence plugin

An implementation of sequence function in ArcadeDB. 

* `createSequence(<name>, <initValue>)`: create a vertex that hold the sequences if not exist and register the  new sequence.
* `sequence(<name>)`: return the current value and increment it.
* `currentSequence(<name>)`: return the current value without increment it.

When the `createSequence()` funtion is called for the first time, the `___sequences` type is created if not exist in the current database. In that type will be holded all the sequences.