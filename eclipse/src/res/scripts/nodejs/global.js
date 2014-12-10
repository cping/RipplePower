//Agregamos el objeto process
global.process = {
	stdout : Shareppy.stdout(),
	stderr : Shareppy.stderr()
};

global.require = function(mod){
	var module = Shareppy.find_module(mod);
	if(module){
		module.exports = {};
		return Shareppy.run_module(module);
	}
	throw "Cannot find module '" + mod + "'";
}

global.require.resolve = function(mod){
	var module = Shareppy.find_module(mod);
	if(module){
		return module.resolve;
	}
	throw "Cannot find module '" + mod + "'";
}

global.util = global.require('util');
//global.console = global.require('console');
//global.stream = global.require('stream');
global.buffer = global.require('buffer');
global.Buffer = global.buffer.Buffer;
global.SlowBuffer = global.buffer.SlowBuffer;
