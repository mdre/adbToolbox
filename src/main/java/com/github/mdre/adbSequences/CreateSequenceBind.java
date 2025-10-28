/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.mdre.adbSequences;

import com.arcadedb.database.Database;
import com.arcadedb.database.DatabaseInternal;
import com.arcadedb.database.Identifiable;
import com.arcadedb.event.BeforeRecordCreateListener;
import com.arcadedb.graph.MutableVertex;
import com.arcadedb.query.sql.executor.CommandContext;
import com.arcadedb.query.sql.function.SQLFunctionAbstract;
import com.arcadedb.schema.DocumentType;
import com.arcadedb.schema.Type;

/**
 *
 * @author mdre
 */
public class CreateSequenceBind extends SQLFunctionAbstract {
    final String DEFAULTSEQUENCETYPENAME = "___sequencesBinds";
    
    public CreateSequenceBind() {
        super("createSequenceBind");
    }

    @Override
    public Object execute(Object self, Identifiable currentRecord, Object currentResult, Object[] params, CommandContext context) {
        // check if the ___sequence vertex exists or create ir
        DatabaseInternal db = context.getDatabase();
        
        if (!db.getSchema().existsType(DEFAULTSEQUENCETYPENAME)) {
            db.transaction(() -> {
                DocumentType sequenceType = db.getSchema().createVertexType(DEFAULTSEQUENCETYPENAME);
                sequenceType.createProperty("sequenceType", Type.STRING);
                sequenceType.createProperty("sequenceProperty", Type.STRING);
                sequenceType.createProperty("sequenceName", Type.STRING);
            });
        }
        
        // verify the parameters.
        if (params.length < 3)
            return null;
        
        final Object sequenceType = params[0];
        final Object sequenceProperty = params[1];
        final Object sequenceName = params[2];
        Object result = null;
        
        if (sequenceName == null )
            return null;
        else if (!(sequenceName instanceof String))
            return null;
        else if (!(sequenceType instanceof String))
            return null;
        
        db.transaction(()-> {
            MutableVertex v = db.newVertex(DEFAULTSEQUENCETYPENAME);
            v.set("sequenceType", sequenceType)
             .set("sequenceProperty", sequenceProperty)
             .set("sequenceName", sequenceName)
            .save();
            
        });
        registerListener(db, sequenceType.toString());
        return true;
    }

    public void registerListener(Database db, String sequenceType) {
            
            db.getSchema()
                .getType(sequenceType)
                .getEvents()
                .registerListener((BeforeRecordCreateListener) record -> {
                                // for each property of the type, set the sequence.
                                String tn = record.asVertex().getTypeName();
                                Database tdb = record.getDatabase();
                                tdb.command("sql", "select from ___sequencesBinds where sequenceType = ?", tn)
                                        .vertexStream().forEach((sb) -> {
                                            // process each sequenceBind
                                            String prop = sb.getString("sequenceProperty");
                                            String sname = sb.getString("sequenceName");
                                            
                                            int sv = tdb.command("sql", "select sequence(?) as value",sname)
                                                        .next()
                                                        .getProperty("value");
                                           
                                            record.asVertex().modify().set(prop, sv);
                                        });
                                return true;
                            }
                );        
            
    }
    
    @Override
    public String getSyntax() {
        return "createSequenceBind(<type>, <property>, <sequence>)";
    }
    
}
