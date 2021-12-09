(function () {
	angular.module('pdm')
	  .directive('dialogboxdirective', DialogBox);
		
		function DialogBox(DialogBox, $uibModalStack, $rootScope, $q, $sce) {
	    return {
	      templateUrl: '/app/components/dialogBox/dialogBox.html',
	      restrict: 'EA',
	      link: dialogLink
	    };

			function dialogLink(scope, element, attrs) {
	      	var deferred = $q.defer();
	      	scope.msg = $sce.trustAsHtml(element[0].getAttribute('msg'));
	      	scope.type = element[0].getAttribute('type').toUpperCase();
	      	scope.numberDialog = element[0].getAttribute('title');
	      	scope.button = {};
	      	scope.button.yes = false;
	      	scope.button.no = false;
	      	$rootScope.uibmodal[scope.numberDialog].dataButton = '';

	      	if(scope.type =='ERROR'){
	      		scope.title = 'An error has occured';
	      		scope.typeMsg = 'danger';
	      	}else if(scope.type == 'OK'){
	      		scope.title = 'Message:';
	      		scope.typeMsg = 'primary';
	      	}else if(scope.type == 'WARNING'){
	      		scope.title = 'Attention:';
	      		scope.typeMsg = 'warning';
	      	}else if(scope.type == 'CONFIRM'){
	      		scope.title = 'Confirm:';
	      		scope.typeMsg = 'primary';
	      		scope.button.yes = true;
	      		scope.button.no = true;
	      	}

	      	scope.buttonConfirm = function (type, number){
	      		if(type == "yes"){
	      			$rootScope.uibmodal[number].dataButton = 'YES';
	      			$rootScope.uibmodal[number].close();
	      		}else {
	      			$rootScope.uibmodal[number].dataButton = 'NO';
	      			$rootScope.uibmodal[number].close();
	      		}
	      	}

	      	scope.closeDialogBox = function(number){
	      		$rootScope.uibmodal[number].close();
	      	}
	      }
	  };
	}
)();
