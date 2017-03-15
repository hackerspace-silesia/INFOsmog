(function () {
    var _timeCtrl = function ($interval, $scope) {
        $scope.time = '00:00';
        
        var _update = function () {
            $scope.time = moment().format('HH:mm');
        };
        
        _update();
        $interval(_update, 1000);
    };
    
    angular.module('controllers').controller('timeCtrl', ['$interval', '$scope', _timeCtrl]);
})();