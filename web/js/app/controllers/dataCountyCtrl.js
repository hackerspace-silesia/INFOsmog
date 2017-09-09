(function () {
    var _dataCountyCtrl = function ($interval, $scope, $timeout, countyService, googleChartsService, smogCountyService) {
        var _updateCountyData = function () {
            return smogCountyService.getData($scope.counties.selected).then(function (data) {
                var _pm10MeasurementTypeData;
                var _no2MeasurementTypeData;

                var _measurementName;

                for (var i = 0; i < data.measurementTypes.length && (!_pm10MeasurementTypeData || !_no2MeasurementTypeData); i++) {
                    _measurementName = data.measurementTypes[i].name;

                    if (_measurementName === 'PM10') {
                        _pm10MeasurementTypeData = data.measurementTypes[i];
                    } else if (_measurementName === 'NO2') {
                        _no2MeasurementTypeData = data.measurementTypes[i];
                    }
                }

                $scope.data = $scope.data || {};

                $scope.data.weather = $scope.data.weather || {};

                $scope.data.weather.degree = data.weatherDegree;
                $scope.data.weather.type = data.weatherType;

                $scope.data.thresholds = $scope.data.thresholds || {};

                $scope.data.thresholds.acceptable = Number.MAX_VALUE;
                $scope.data.thresholds.informative = Number.MAX_VALUE;
                $scope.data.thresholds.alarm = Number.MAX_VALUE;

                $scope.data.chartData = [];

                var _addData = function (measurementTypeData) {
                    $scope.data.thresholds.acceptable = Math.min($scope.data.thresholds.acceptable, measurementTypeData.thresholds.acceptable);
                    $scope.data.thresholds.informative = Math.min($scope.data.thresholds.informative, measurementTypeData.thresholds.informative);
                    $scope.data.thresholds.alarm = Math.min($scope.data.thresholds.alarm, measurementTypeData.thresholds.alarm);

                    var _indexCurrentData = 0;
                    var _indexNewData = 0;

                    var _hourCurrentData;
                    var _hourNewData;

                    while (_indexCurrentData < $scope.data.chartData.length && _indexNewData < measurementTypeData.measurements.length) {
                        _hourCurrentData = $scope.data.chartData[_indexCurrentData].hour;
                        _hourNewData = measurementTypeData.measurements[_indexNewData].hour;

                        if (_hourCurrentData < _hourNewData) {
                            _indexCurrentData++;
                        } else if (_hourCurrentData === _hourNewData) {
                            $scope.data.chartData[_indexCurrentData].value = Math.max($scope.data.chartData[_indexCurrentData].value, measurementTypeData.measurements[_indexNewData].value);

                            _indexCurrentData++;
                            _indexNewData++;
                        } else {
                            $scope.data.chartData.splice(_indexCurrentData, 0, {
                                hour: measurementTypeData.measurements[_indexNewData].hour,
                                value: measurementTypeData.measurements[_indexNewData].value
                            });

                            _indexCurrentData++;
                            _indexNewData++;
                        }
                    }

                    for (var i = _indexNewData; i < measurementTypeData.measurements.length; i++) {
                        $scope.data.chartData.push({
                            hour: measurementTypeData.measurements[i].hour,
                            value: measurementTypeData.measurements[i].value
                        });
                    }
                };

                if (_pm10MeasurementTypeData || _no2MeasurementTypeData) {
                    if (_pm10MeasurementTypeData && _no2MeasurementTypeData) {
                        _addData(_pm10MeasurementTypeData);
                        _addData(_no2MeasurementTypeData);
                    } else {
                        _addData(_pm10MeasurementTypeData || _no2MeasurementTypeData);
                    }
                }
            });
        };

        $scope.counties = {};

        $scope.counties.list = [];
        $scope.counties.selected = 0;

        $scope.changeCounty = function () {
            _updateCountyData();

            countyService.storeId($scope.counties.selected);
        };

        countyService.getList().then(function (counties) {
            $scope.counties.list = counties;
            $scope.counties.selected = countyService.getId(counties);
        }).then(_updateCountyData).then(googleChartsService.load).then(function () {
            var _now = moment($scope.data.now);

            var _nowEndOfHour = _now.clone();
            _nowEndOfHour.endOf('hour')

            var _toMilis = function (minutes) {
                return minutes * 60 * 1000;
            };

            $timeout(function () {
                _updateCountyData();
                $interval(_updateCountyData, _toMilis(60));
            }, _nowEndOfHour.diff(_now) + _toMilis(15));
        });
    };

    angular.module('controllers').controller('dataCtrl', ['$interval', '$scope', '$timeout', 'countyService', 'googleChartsService', 'smogCountyService', _dataCountyCtrl]);
})();