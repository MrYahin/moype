(function(){
	angular.module('appAssistant')
	.factory('httpInterceptorOrderProductionList', function($q, $location, $templateCache) {
		return {
			request: function(config){
				if (config.method === 'GET' && $templateCache.get(config.url) === undefined)
        			config.url += '?ver=' + new Date().getTime();
				return config;
			}
	  	};
	});
})();