(function() {
    'use strict';

    angular.module('pdm', ["ngRoute", "ui.bootstrap"])
    .config(config);
    
    function config($routeProvider, $locationProvider, $httpProvider) {
        $locationProvider.hashPrefix('');
		$routeProvider.when('/', {
			templateUrl : '/app/pdm/index.html'
		}).otherwise({
			redirectTo : '/'
		});
		$httpProvider.interceptors.push('httpInterceptor'); 
	}
})();