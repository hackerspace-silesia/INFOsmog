(function () {
    var _signingInCtrl = function ($scope, $uibModal) {
        $scope.showForm = function () {
            $uibModal.open({
                size: 'signin',
                scope: $scope,
                controller: 'signingInFormCtrl',
                templateUrl: 'signingInForm.html'
            });
        };
    };
    
    angular.module('controllers').controller('signingInCtrl', ['$scope', '$uibModal', _signingInCtrl]);
})();