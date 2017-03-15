(function () {
    var _weatherCtrl = function ($scope) {
        $scope.type = {};
        
        $scope.type.rainy = 1;
        $scope.type.stormyRainy = 2;
        $scope.type.cloudyTornado = 3;
        $scope.type.tornadoThunder = 4;
        $scope.type.suny = 5;
        $scope.type.cloudy = 6;
        $scope.type.snowy = 7;
        $scope.type.cloud = 8;
        $scope.type.stormyRainyTornado = 9;
    };
    
    angular.module('controllers').controller('weatherCtrl', ['$scope', _weatherCtrl]);
})();