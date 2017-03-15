(function () {
    var _signingInFormCtrl = function ($scope, $uibModalInstance, signingInService) {
        var _initUser = function () {
            $scope.user = {};
        };
        
        $scope.signIn = function () {
            signingInService.signIn($scope.user);
        };
        
        $scope.cancel = function ($event) {
            $event.preventDefault();
            $uibModalInstance.close();
            _initUser();
        };
        
        _initUser();
    };
    
    angular.module('controllers').controller('signingInFormCtrl', ['$scope', '$uibModalInstance', 'signingInService', _signingInFormCtrl]);
})();