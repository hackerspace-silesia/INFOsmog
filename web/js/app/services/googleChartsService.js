(function () {
    var _googleChartsService = function ($interval, $q, smogService) {
        var _googleCharts = {};
        
        var _initialized = false;
        var _duringInitializing = false;
        
        _googleCharts.load = function (wait) {
            var _deferred = $q.defer();
            var _promise = _deferred.promise;
            
            if (!_initialized) {
                if (!_duringInitializing && !wait) {
                    _duringInitializing = true;
                    
                    google.charts.load('current', {
                        packages: [
                            'corechart'
                        ]
                    });
                    
                    google.charts.setOnLoadCallback(function () {
                        _deferred.resolve();
                        _initialized = true;
                    });
                } else {
                    var _checkingIfInitialized = $interval(function () {
                        if (_initialized) {
                            $interval.cancel(_checkingIfInitialized);
                            _deferred.resolve();
                        }
                    }, 100);
                }
            } else {
                _deferred.resolve();
            }
            
            return _promise;
        };
        
        _googleCharts.toSmogDataTable = function (smogData, acceptableThresold, informThreshold, alarmThreshold) {
            var _dataArray = [];
                    
            _dataArray.push([
                'Godzina', 
                'Poziom', { 
                    role: 'style' 
                }, { 
                    role: 'annotation'
                }
            ]);

            var _entry;
            var _color;
            var _percentage;
            
            if (smogData) {
                    for (var i = 0; i < smogData.length; i++) {
                    _entry = smogData[i];

                    _color = '#4CAF50';

                    if (_entry.value >= alarmThreshold) {
                        _color = '#FF2B00';
                    } else if (_entry.value >= informThreshold) {
                        _color = '#FFAB00';
                    }

                    _percentage = smogService.percentage(_entry.value, acceptableThresold);

                    _dataArray.push([
                        ('0' + _entry.hour).slice(-2) + ':00', 
                        _percentage, 
                        _color, 
                        _percentage + '%'
                    ]);
                }
            }

            return google.visualization.arrayToDataTable(_dataArray);
        };
        
        return _googleCharts;
    };
    
    angular.module('services').factory('googleChartsService', ['$interval', '$q', 'smogService', _googleChartsService]);
})();