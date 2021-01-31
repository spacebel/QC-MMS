/**
 * CatalogueIPC -------------
 * 
 * @author Christophe Noel - Spacebel
 * @date March 2013 Inter Portlets Communication
 * 
 */
var Catalogue = new Object();
Catalogue.IPC = new Object();
Catalogue.IPC  = {

	onComponentRestore : function(restoreMessage) {
		// TODO implement
		//alert("Catalogue Comp Restore");
		//alert(restoreMessage.data);
		//alert("fire message:"+JSON.stringify(restoreMessage.data));
		document.getElementById('catState').value=JSON.stringify(restoreMessage.data);
		jQuery("input[name*=restoreStateBtn]").click();
	},
	getMyInstanceId : function() {
		return "Catalogue";
	},
 
	
	onComponentStateRequest : function(message) {
		//alert("Catalogue State Request");
		stateId = message.data;
		jQuery("input[name*=getStateBtn]").click();
		
	},
};