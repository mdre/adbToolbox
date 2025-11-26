/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.mdre.adbToolbox;

import com.arcadedb.database.Database;
import com.arcadedb.database.DatabaseInternal;
import com.arcadedb.database.Identifiable;
import com.arcadedb.event.BeforeRecordUpdateListener;
import com.arcadedb.graph.MutableVertex;
import com.arcadedb.query.sql.executor.CommandContext;
import com.arcadedb.query.sql.function.SQLFunctionAbstract;
import com.arcadedb.schema.DocumentType;
import com.arcadedb.schema.Type;

/**
 *
 * @author mdre
 */
public class CreateAutoincrementBind extends SQLFunctionAbstract {
    final String DEFAULTSEQUENCETYPENAME = "___autoincrementBinds";
    
    public CreateAutoincrementBind() {
        super("createAutoincrementBind");
    }

    @Override
    public Object execute(Object self, Identifiable currentRecord, Object currentResult, Object[] params, CommandContext context) {
        // check if the ___sequence vertex exists or create ir
        DatabaseInternal db = context.getDatabase();
        
        if (!db.getSchema().existsType(DEFAULTSEQUENCETYPENAME)) {
            db.transaction(() -> {
                DocumentType sequenceType = db.getSchema().createVertexType(DEFAULTSEQUENCETYPENAME);
                sequenceType.createProperty("Type", Type.STRING);
                sequenceType.createProperty("Property", Type.STRING);
            });
        }
        
        // verify the parameters.
        if (params.length < 2)
            return null;
        
        final Object autoincType = params[0];
        final Object autoincProperty = params[1];
        
        Object result = null;
        
        if (!(autoincType instanceof String))
            return null;
        if (!(autoincProperty instanceof String))
            return null;
        
        db.transaction(()-> {
            MutableVertex v = db.newVertex(DEFAULTSEQUENCETYPENAME);
            v.set("Type", autoincType)
             .set("Property", autoincProperty)
            .save();
            
        });
        registerListener(db, autoincType.toString());
        return true;
    }

    public void registerListener(Database db, String onType) {
            
            db.getSchema()
                .getType(onType)
                .getEvents()
                .registerListener((BeforeRecordUpdateListener) record -> {
                                // for each property of the type, set the autoinc.
                                String tn = record.asVertex().getTypeName();
                                Database tdb = record.getDatabase();
                                tdb.command("sql", "select from ___autoincrementBinds where Type = ?", tn)
                                        .vertexStream().forEach((sb) -> {
                                            // process each sequenceBind
                                            String prop = sb.getString("Property");
                                            
                                            Long autoinc = record.asVertex().getLong(prop);
                                            
                                            if (autoinc == null) autoinc = 0l;
                                            
                                            record.asVertex().modify().set(prop, autoinc+1);
                                        });
                                return true;
                            }
                );        
            
    }
    
    @Override
    public String getSyntax() {
        return "createAutoincrementBind(<type>, <property>)";
    }
    
}
