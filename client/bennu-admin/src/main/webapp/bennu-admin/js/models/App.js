define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
	urlRoot : "../api/bennu-portal/apps",
	
	url : function() {
		return this.urlRoot + "/" + this.get("path");
	},
	
	defaults: {
		title : "Title",
		description : "Description",
		path : "/path",
		accessExpression : "anyone",
		functionalities: []
    },
});

});