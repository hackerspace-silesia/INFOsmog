(function () {
    var _infoCtrl = function ($scope, smogService) {
        var _getValue = function () {
            return $scope.data.chartData[$scope.data.chartData.length - 1].value;
        };
        
        $scope.isAcceptable = function () {
            var _isAcceptable = true;
            
            if ($scope.data) {
                _isAcceptable = _getValue() < $scope.data.thresholds.informative;
            }
            
            return _isAcceptable;
        };
        
        $scope.isInform = function () {
            return !$scope.isAcceptable() && !$scope.isAlarm();
        };
        
        $scope.isAlarm = function () {
            var _isAlarm = false;
            
            if ($scope.data) {
                _isAlarm = _getValue() >= $scope.data.thresholds.alarm;
            }
            
            return _isAlarm;
        };
        
        $scope.getBackgroundClass = function () {
            return { 
                'smog-green': $scope.isAcceptable(), 
                'smog-yellow': $scope.isInform(), 
                'smog-red': $scope.isAlarm() 
            };
        };
        
        $scope.getExceededPercentage = function () {
            if (!$scope.isAcceptable()) {
                return smogService.percentage(_getValue(), $scope.data.thresholds.acceptable) - 100;
            }
        };
    };
    
    angular.module('controllers').controller('infoCtrl', ['$scope', 'smogService', _infoCtrl]);
})();