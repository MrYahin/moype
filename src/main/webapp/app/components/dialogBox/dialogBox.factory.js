(function() {
	angular.module('pdm')
	  	.factory('DialogBox', function($uibModal, $rootScope, $q, $document) {
	  		var msg, type;
	  		var numberDialog = 0;
	  		$rootScope.uibmodal = [];

		    return {
		      show: show,
		      close: close,
		      setMsg: setMsg,
		      getMsg: getMsg,
		      setType: setType,
		      getType: getType,
		      setNumber: setNumber,
		      getNumber: getNumber
		    };

		    function show(_msg, _type){
		    	var vm = this;
		    	msg = _msg;
		    	type = _type;
		    	numberDialog += 1;
			    $rootScope.uibmodal[numberDialog] = $uibModal.open({
			      	animation: true,
			      	template: "<dialogboxdirective title='"+ numberDialog +"' msg='" + msg + "' type='"+ type +"'></dialogboxdirective>",
			      	backdrop: 'static',
    				keyboard: false,
			      	size: '300px'
			    });
			    vm.numberDialog = numberDialog;

			    return $q(function(resolve, reject) {
			    	$rootScope.uibmodal[vm.numberDialog].closed.then(function(){
			    		if(type.toUpperCase() == 'CONFIRM'){
			    			if($rootScope.uibmodal[vm.numberDialog].dataButton=="NO"){
								reject();
			    			}else{
			    				resolve();
			    			}
			    		}else{
			    			resolve($rootScope.uibmodal[vm.numberDialog].data);
			    		}
			    	}, function(){
			    		reject();
			    	});
  				});
		    }

		    function close(){
		    	$uibModal.close('cancel');
		    }
		    function setMsg(value){
		    	msg = value;
		    }
		    function getMsg(){
		    	return msg;
		    }
		    function setType(value){
		    	type = value;
		    }
		    function getType(){
		    	return type;
		    }
		    function setNumber(value){
		    	numberDialog = value;
		    }
		    function getNumber(){
		    	return numberDialog;
		    }
	  	});	
	}
)();