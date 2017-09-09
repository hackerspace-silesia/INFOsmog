(function () {
    var _dataSensorCtrl = function ($interval, $scope, $timeout, sensorService, googleChartsService) {

        var _updateSensorData = function () {

                $scope.sensors = {};

                $scope.sensors.list = [];
                $scope.sensors.selected = 0;

                sensorService.getList().then(function (sensors) {
                    $scope.sensors.list = sensors;
                    $scope.sensors.selected = sensorService.getId(sensors);
                }).then(_updateSensorData).then(googleChartsService.load).then(function () {
                    var _now = moment($scope.data.now);

                    var _nowEndOfHour = _now.clone();
                    _nowEndOfHour.endOf('hour')

                    var _toMilis = function (minutes) {
                        return minutes * 60 * 1000;
                    };

                    $timeout(function () {
                        _updateSensorData();
                        $interval(_updateSensorData, _toMilis(60));
                    }, _nowEndOfHour.diff(_now) + _toMilis(15));
                });
        }
    };

    angular.module('controllers').controller('dataCtrl', ['$interval', '$scope', '$timeout', 'countyService', 'googleChartsService', 'smogSensorService', _dataSensorCtrl]);
})();