global.process = {
	stdout : nodejs.stdout(),
	stderr : nodejs.stderr()
};

global.require = function(mod){
	var module = nodejs.find_module(mod);
	if(module){
		module.exports = {};
		return nodejs.run_module(module);
	}
	throw "Cannot find module '" + mod + "'";
}

global.require.resolve = function(mod){
	var module = nodejs.find_module(mod);
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
