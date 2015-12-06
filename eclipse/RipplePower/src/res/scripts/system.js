importPackage(java.awt, java.awt.event)
importPackage(Packages.javax.swing)
importPackage(java.io)
importClass(java.lang.System)

function main(){

	var ui = app.getUI();

	app.activePlugin("scripts/json.js");
	app.activePlugin("scripts/date.js");
	app.activePlugin("scripts/util.js");

	app.launch();
	
}
