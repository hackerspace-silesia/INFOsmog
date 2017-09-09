(function () {
    var _infoSensorCtrl = function ($scope, smogSensorService) {

        var _getSensorValue = function () {
            return $scope.data.chartData[$scope.data.chartData.length - 1].value;
        };

        $scope.isAcceptableSensor = function () {
            var _isAcceptableSensor = true;

            if ($scope.data) {
                _isAcceptableSensor = _getSensorValue() < $scope.data.thresholds.informative;
            }

            return _isAcceptableSensor;
        };

        $scope.isInform = function () {
            return!scope.isAcceptableSensor() && !$scope.isAlarmBySensor();
        };

        $scope.isAlarmBySensor = function () {
            var _isAlarmBySensor = false;

            if ($scope.data) {
                _isAlarmBySensor = _getSensorValue() >= $scope.data.thresholds.alarm;
            }

            return _isAlarmBySensor;
        };

        $scope.getBackgroundClass = function () {
            return {
                'smog-green': $scope.isAcceptableSensor(),
                'smog-yellow': $scope.isInform(),
                'smog-red': $scope.isAlarmBySensor()
            };
        };

        $scope.getExceededBySensorInPercentage = function () {
            if (!$scope.isAcceptableSensor()) {
                return smogSensorService.percentage(_getSensorValue(), $scope.data.thresholds.acceptable) - 100;
            }
        };
    };

    angular.module('controllers').controller('infoSensorCtrl', ['$scope', 'smogSensorService', _infoSensorCtrl]);
})();