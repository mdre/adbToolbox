# ArcadeDB Sequence plugin

An implementation of sequence function in ArcadeDB. 

* `createSequence(<name>, <initValue>)`: create a vertex that hold the sequences if not exist and register the  new sequence.
* `sequence(<name>)`: return the current value and increment it.
* `currentSequence(<name>)`: return the current value without increment it.
* `createSequenceBind(<type name>,<property>,<sequenceName>): bind the Type property with the sequence.

When the `createSequence()` funtion is called for the first time, the `___sequences` type is created if not exist in the current database. In that type will be holded all the sequences.

To register this plugin, just copy the jar to the lib directory in the ArcadeDB an add a launch like this:

```bash
#!/bin/bash
cd /opt/arcadedb
export ARCADEDB_SETTINGS="-Darcadedb.server.plugins=ADBSequences:com.github.mdre.adbSequences.ADBSequencesPlugin"
./bin/server.sh $1
```

And when you start the server you should see a line like this: 

```
2025-10-23 08:34:03.387 INFO  [ArcadeDBServer] <ArcadeDB_0> Starting ArcadeDB Server in development mode with plugins [ADBSequences] ...
...
...
Plugin configured

ADBSequences plugin started

2025-10-23 08:36:19.037 INFO  [ArcadeDBServer] <ArcadeDB_0> - ADBSequences plugin started

``` 

Now, in the studio for example, you could call the funcions.

To create a sequence that start on 100:
```
select createSequence("testSequence",100);
``` 

and to call and increment it: 

```
select sequence("testSequence");
``` 
this will return `100` and the next call will return `101` and so on.

If you need to know what is the current next value without increment it use:

``` 
select currentSequence("testSequence");
``` 

Finally, To bind a sequence to a type property use:
 
``` 
select createSequenceBind("Serial","s1","testSequence");
``` 
This bind Serial.s1 with the testSquence and will be filled on create by the database.

Multiple bind could point to the same sequence.


