(function () {
    var _infoCountyCtrl = function ($scope, smogCountyService) {
        var _getGIOSValue = function () {
            return $scope.data.chartData[$scope.data.chartData.length - 1].value;
        };

        $scope.isAcceptableGIOS = function () {
            var _isAcceptableGIOS = true;

            if ($scope.data) {
                _isAcceptableGIOS = _getGIOSValue() < $scope.data.thresholds.informative;
            }

            return _isAcceptableGIOS;
        };

        $scope.isInform = function () {
            return !$scope.isAcceptableGIOS() && !$scope.isAlarmByGIOS();
        };

        $scope.isAlarmByGIOS = function () {
            var _isAlarmByGIOS = false;

            if ($scope.data) {
                _isAlarmByGIOS = _getGIOSValue() >= $scope.data.thresholds.alarm;
            }

            return _isAlarmByGIOS;
        };

        $scope.getBackgroundClass = function () {
            return {
                'smog-green': $scope.isAcceptableGIOS(),
                'smog-yellow': $scope.isInform(),
                'smog-red': $scope.isAlarmByGIOS()
            };
        };

        $scope.getExceededByGIOSInPercentage = function () {
            if (!$scope.isAcceptableGIOS()) {
                return smogCountyService.percentage(_getGIOSValue(), $scope.data.thresholds.acceptable) - 100;
            }
        };
    };

    angular.module('controllers').controller('infoCountyCtrl', ['$scope', 'smogCountyService', _infoCountyCtrl]);
})();