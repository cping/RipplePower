package org.ripple.power.hft.command.script;

import java.util.Vector;

import org.address.collection.ArrayList;
import org.ripple.power.hft.command.ExprBlock;
import org.ripple.power.hft.command.ExprFuncCall;
import org.ripple.power.hft.command.ExprVal;

public class ScriptExecute {
    
    
    private ScriptParser parser;
    private ExprBlock bin;
    private ScriptContext global;
    
    public ScriptExecute() {
        global = new ScriptContext(this);
        parser = new ScriptParser(this, global);
        bin = parser.root;
    }
    
    public ScriptExecute(ScriptContext global) {
        this.global = global;
        parser = new ScriptParser(this, global);
        bin = parser.root;
    }
    
    public void command(String s){
        parser.code.addLines(s);
    }
    
    public void parse() {
        parser.parse();
    }
    
    public ExprBlock parse(String line) {
        clear();
        return parser.parse(line);
    }
    
    public ScriptObject run() {
		return bin.eval(global);
    }

    public ScriptObject evaluateExpression(String expr) {
        ExprBlock bin = parser.parse(expr);
        return bin.eval(global);
    }
    
    public void reset(){
        global.clear();
        
    }
    
    public void clear(){
        parser.reset();
        parser.clear();
        bin = parser.root;
    }
    
    public String process() {
        return bin.toString();
    }
    
    public ScriptContext getContext() {
    	return global;
    }
    
    public void exit(ScriptObject o) {
        throw new RetException(o);
    }
    
    protected ScriptObject getVar(String name) {
    	return ScriptBuild.vars.containsKey(name) ? (ScriptObject) ScriptBuild.vars.get(name) : ScriptObject.FSUNDEF;
    }
    
    protected Object getVar(String name,Object index) {
    	return ScriptObject.FSUNDEF;
    }
    
  
    protected boolean setVar(String name,Object value){
        return false;
    }
    
    protected boolean setVar(String name,Object index,Object value) {
    	return false;
    }
       
    protected Object callFunction(String name, Vector<?> params) {
        throw new ScriptException("Unrecognized External: " + name);
    }
    
    public final void setScriptVar(String name, ScriptObject value) {
        global.setVar(name,value);
    }
    
    public final ScriptObject getScriptVar(String name) {
        return global.getVar(name);
    }
    
    public final Object callScriptFunction(String name,ArrayList params) {
    	return new ExprFuncCall(new ExprVal(name), params).eval(global);
    }
    
}
