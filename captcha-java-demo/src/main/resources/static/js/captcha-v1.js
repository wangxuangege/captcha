/**
 * 银商验证码组件
 * （依赖jquery，版本不限）
 *
 * @version v1
 * @author xinquan.huangxq
 */
(function(window, undefined) {
    /**
     * 版本信息
     *
     * @type {string}
     */
    var core_version = "v1";
    /**
     * document对象
     *
     * @type {HTMLDocument}
     */
    var document = window.document;
    /**
     * 验证码生成
     *
     * @param id 验证码填充到的组件
     * @param context 生成验证码的信息
     * @returns {myCaptcha.init}
     */
    var myCaptcha = function(id, context) {
        return new myCaptcha.fn.init(id, context);
    };
    myCaptcha.fn = myCaptcha.prototype = {
        /**
         * 版本信息
         */
        version:core_version,
        /**
         * 初始化属性
         *
         * @param id
         * @param context
         */
        constructor:function(id, context) {
            if (this.initFlag) {
                return;
            }
            if (!id) {
                throw "验证码容器ID不能为空";
            }
            var jquerymyCaptcha = $("#" + id);
            if (!jquerymyCaptcha) {
                throw "验证码容器不存在";
            }
            // 验证码容器
            this.jqueryContainer = jquerymyCaptcha;
            if (!context.captchaId) {
                throw "验证码使用场景标示不能为空";
            }
            this.captchaId = context.captchaId;
            if (!context.challengeId) {
                throw "验证码请求标示不能为空";
            }
            this.challengeId = context.challengeId;
            if (!context.verifyCallback) {
                throw "验证码回调为空，验证码无法工作使用";
            }
            this.verifyCallback = context.verifyCallback;
            this.width = context.width || this.width;
            this.height = context.height || this.height;
            this.serviceGateway = context.serviceGateway || this.serviceGateway;
        },
        /**
         * 初始化
         *
         * @param id
         * @param context
         */
        init:function(id, context) {
            if (this.initFlag) {
                return;
            }
            // 初始化验证码属性
            this.constructor(id, context);
            // 渲染组件
            this.rendering();
            // 添加事件响应
            this.addEvent();
            // 设置初始化字段为true
            this.initFlag = true;
        },
        /**
         * 渲染
         */
        rendering:function() {
            if (this.initFlag) {
                return;
            }
            // 渲染
            this.jqueryContainer.css("position", "relative").css("width", this.width).css("height", this.height);
            // 验证码刷新渲染
            this.freshImg = document.createElement("img");
            $(this.freshImg).attr("src", this.freshImgBase64).css("width", "24px").css("height", "24px").css("position", "absolute").css("top", "0px").css("right", "0px");
            this.jqueryContainer.append(this.freshImg);
            // 验证码渲染
            this.myCaptchaImg = document.createElement("img");
            $(this.myCaptchaImg).attr("src", this.myCaptchaShowSrc()).css("width", this.width).css("height", this.height);
            this.jqueryContainer.append(this.myCaptchaImg);
        },
        /**
         * 添加事件响应
         */
        addEvent:function() {
            if (this.initFlag) {
                return;
            }
            // this引用改变
            var _this = this;
            $(this.freshImg).click(function(e) {
                _this.fresh();
            });
            $(this.myCaptchaImg).click(function(e) {
                _this.clickmyCaptcha(e);
            });
        },
        /**
         * 验证码show src获取
         *
         * @returns {string}
         */
        myCaptchaShowSrc:function() {
            return this.serviceGateway + "/show/" + this.challengeId;
        },
        /**
         * 验证码刷新 src获取
         *
         * @returns {string}
         */
        myCaptchaRefreshSrc:function() {
            return this.serviceGateway + "/fresh/" + this.challengeId + "?t" + new Date().getTime();
        },
        /**
         * 验证码提交校验的url
         */
        myCaptchaValidateUrl:function() {
            return this.serviceGateway + "/validate/first";
        },
        /**
         * 验证码刷新
         */
        fresh:function() {
            // 更新验证码src
            $(this.myCaptchaImg).attr("src", this.myCaptchaRefreshSrc());
            // 清理点击信息
            this.clickedPointArray = [];
            $.each(this.clickedImgArray, function(index, img) {
                $(img).remove();
            });
            this.clickedImgArray = [];
            this.clickedIndexArray = [];
            // 刷新后状态更新
            this.status = "SHOW";
        },
        /**
         * 点击验证码事件
         *
         * @param e
         */
        clickmyCaptcha:function(e) {
            if (this.status == "VALID") {
                // 如果校验成功，那么直接返回
                return;
            }
            // 点击相对坐标
            var eventPos = getEventPos(e);
            var myCaptchaContainPos = getDOMPos(this.jqueryContainer[0]);
            var x = eventPos.x - myCaptchaContainPos.x - 9;
            var y = eventPos.y - myCaptchaContainPos.y - 9;
            if (!this.clickValidate(x + 9, y + 9)) {
                // 点击校验失败，那么点击不添加新的点击点
                return;
            }
            // 创建点击img
            var clickedImg = document.createElement("img");
            var _this = this;
            $(clickedImg).attr("src", _this.clickedImgBase64).css("width", "18px").css("height", "18px").css("position", "absolute").css("left", x).css("top", y).click(function(e) {
                // 删除当前DOM元素
                $(clickedImg).remove();
                // 删除坐标
                for (var i = _this.clickedPointArray.length - 1; i >= 0; --i) {
                    var point = _this.clickedPointArray[i];
                    if (point.x + "px" == $(clickedImg).css("left") && point.y + "px" == $(clickedImg).css("top")) {
                        // 清理
                        _this.clickedPointArray.splice(i, 1);
                        _this.clickedImgArray.splice(i, 1);
                        _this.clickedIndexArray.splice(i, 1);
                        return;
                    }
                }
            });
            this.jqueryContainer.append(clickedImg);
            // 记录点击坐标和图表
            this.clickedPointArray.push({
                x:x,
                y:y
            });
            this.clickedImgArray.push(clickedImg);
        },
        /**
         * 点击校验
         * 防止点击无效的点或者过多的点，若校验失败，返回false，校验成功，返回true
         */
        clickValidate:function(x, y) {
            var gap = 5;
            // 如果点击过多无效的坐标，直接返回
            if (this.clickedPointArray.length >= this.verticalSize * this.horizontalSize) {
                return false;
            }
            // title的高度
            var titlePx = this.titleScale * this.height;
            // 图片是哪个区域
            var i = 0, j = 0;
            // 不允许点击title部分
            if (y <= titlePx) {
                return false;
            }
            var perWidth = this.width / this.horizontalSize;
            var perHeight = (this.height - titlePx) / this.verticalSize;
            while (x > perWidth) {
                x = x - perWidth;
                ++i;
            }
            y = y - titlePx;
            while (y > perHeight) {
                y = y - perHeight;
                ++j;
            }
            // 边界附近
            if (x <= gap || x >= perWidth - gap) {
                return false;
            }
            if (y <= gap || y >= perHeight - gap) {
                return false;
            }
            // 同一张小图片只能点击1次
            var k = i * this.horizontalSize + j;
            if ($.inArray(k, this.clickedIndexArray) > -1) {
                return false;
            }
            this.clickedIndexArray.push(k);
            return true;
        },
        /**
         * 提交验证码校验
         */
        validate:function() {
            if (this.status == "VALID") {
                // 如果校验成功，那么直接回调用户请求
                if (this.verifyCallback) {
                    this.verifyCallback({
                        valid:true
                    });
                }
                return;
            }
            // 获取用户轨迹
            var trajectory = "000|";
            var _this = this;
            $.each(this.clickedPointArray, function(index, point) {
                var fx = point.x * 1 / _this.width;
                var fy = point.y * 1 / _this.height;
                trajectory += fx + "," + fy + ";";
            });
            if (trajectory == "000|") {
                trajectory += "-1,-1;";
            }
            // 请求参数
            var data = {
                captchaId:this.captchaId,
                challengeId:this.challengeId,
                timestamp:new Date().getTime(),
                trajectory:trajectory,
                version:this.version
            };
            // 签名
            data.signature = md5("captchaId" + data.captchaId + "challengeId" + data.challengeId + "timestamp" + data.timestamp + "trajectory" + data.trajectory + "version" + data.version + "JS-SDK");
            // jsonp跨域提交请求
            $.ajax({
                type:"get",
                url:this.myCaptchaValidateUrl(),
                data:data,
                dataType:"jsonp",
                success:function(ret) {
                    var result = {};
                    if (ret.success) {
                        if (ret.model.access) {
                            result.valid = true;
                            result.errMsg = "";
                            _this.status = "VALID";
                        } else {
                            result.valid = false;
                            result.errMsg = "验证码校验失败";
                            _this.status = "UN_VALID";
                            _this.fresh();
                        }
                    } else {
                        result.valid = false;
                        result.errMsg = ret.errMsg;
                        _this.status = "UN_VALID";
                        _this.fresh();
                    }
                    // 用户自己的回调
                    if (_this.verifyCallback) {
                        _this.verifyCallback(result);
                    }
                }
            });
        },
        /**
         * 校验成功的回调
         */
        verifyCallback:null,
        /**
         * 初始化字段
         */
        initFlag:false,
        /**
         * 验证码点击坐标
         */
        clickedPointArray:[],
        /**
         * 点击验证码生成的DOM元素点
         */
        clickedImgArray:[],
        /**
         * 点击图片索引
         */
        clickedIndexArray:[],
        /**
         * 刷新图标
         */
        freshImg:null,
        /**
         * 验证码图标
         */
        myCaptchaImg:null,
        /**
         * 验证码状态
         * NEW（初始化未渲染状态）、SHOW（渲染状态）、VALID（校验成功状态）、UN_VALID（校验失败状态）
         */
        status:"NEW",
        /**
         * 存放验证码的容器
         */
        jqueryContainer:null,
        /**
         * 验证码宽度
         */
        width:180,
        /**
         * 验证码高度
         */
        height:320,
        /**
         * 验证码服务端网关
         */
        serviceGateway:"",
        /**
         * 验证码使用场景标示
         */
        captchaId:null,
        /**
         * 验证码唯一标示
         */
        challengeId:null,
        /**
         * 水平图片个数
         */
        horizontalSize:3,
        /**
         * 竖直高度图片个数
         */
        verticalSize:2,
        /**
         * title比例
         */
        titleScale:1 / 6,
        /**
         * 点击点图片的base64编码
         */
        clickedImgBase64:"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAMAAAD04JH5AAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAAaVBMVEUAAABDoEdDoEdDoEdDoEdDoEdDoEdDoEdDoEdDoEdDoEdDoEdDoEdDoEdDoEdDoEc4jjw4jjw4jjw4jjw4jjw4jjw4jjw4jjw4jjw4jjw4jjw4jjw4jjw4jjw4jjw5kD1DoEc4jjz///+wahLMAAAAIHRSTlMAQGCAUDAgj7/v368Qn89wMGCAv69QEECf389wII/vgM/7YrQAAAABYktHRCJdZVysAAAAB3RJTUUH3gwKEBomMKsGzAAABS1JREFUeNrtW9t24joMbe73ACmUW8sh//+TpxQ6I8uSLTmhs2ZN9iu2JUuyrFibl5cFCxYs+HsRRXHyQBplPyo6j4uyuiLUTRK1PyC8jbv6yqIvnmuKNm6uPlTP0yHvKq/4uzdWz/BFVMqk382Q5H9S/BeSOa2Q+11PWWE2+YnQ91YsRLOIz/ow8TcUM/ghCRd/M8LUM9nywVeXxXcqbkreSatp5qezXtWssHvbKGF07Sa4IaU2VrOprk07SoM+WIOY2o87stuYCNk+MBBs+aIER+SsKkgDW770UNkqhGhgyS8V6T3GwVOp74YUr6A7Ti1O3tpIzKrw7T92gFbodRtA57/Qiv9Ejs5Dp5mMoigOkP+5iy54FTP/hx2iG7rAdbKZ5GMNpGFgBsAU+VgDYYliOmDifdrpF8tniD8AIyGUkhml0GjrzfB6x3bYvbHD2l65n0ik8f5wHA2czu/MUCOn1ToDVEz+3CDpd2z39OiVygSGAVKF+Bte1949eU0ABzfUgPXr6MAHFQx5JTeBYQDqAtpcRidOlBESuQngsaVOwGH04bIjpsHU5qzpWk8E+uV/YmPPg9WN81ZcuQ0gkk9qAE3gKk16pwGE8sfRPo/QBI4wzJ2WOkvljxcrKbXgIDS8AtAD1r2xFsv/TAjW0oXIB+DisO/uk0KBccCzYY2RsgqAQVYRLHcA7YTa5d0HYBbCSejtolJgPDh8wOailWPMoJM/jtgE0AdcEIAQsOrwo1aBD7wCOAdcMgRZAMfJTit/vODVwTXHlTmOEBDnoN/AdwK4kZgoBF6q8G/KEKTCEHxtMoVWxI/Y6+WPR8X+7oh5G/0XoMCIaxPgYVoB4CQcJQEhYF9JIBW1WgVeQxQ4o0XAMYh8CuAB6ixww6BVoOEHhMjXK1D+8wp0f1oBEIT4KviZIJz7GG5mVGCWRATuY1I+TMW4cNXVYw/g9b2p2HEZaSrib5zQGuAyYmqy3KFiwHWMSyL/dQxthL8KAoJgzYcY9/AKwhR/PukLgqNjde7hG2Qiq2hSZwLr0wTYlytKXWX5Rin/gssR+P7PyDdKd+vTUGkCywDgw4R/sgWpwooTXWFuGQDWQ/zjf+fwgS4dW+808LOP/ziFzwhWoLwrcsHWtTc2BMwnIvvjQe6E05trZccDhfGybD/SSW+Ey9qaCh/qXC+FntcsWT4k5MMXmurFhcppApEGhHzDAO7mVecZufHKPxLyoQE8TQujW0EN3XsS0pZ6LIYvVL6eBQxDMmO9udxwOVNTjCaY773eeK2mb609m5I+6MYJ7Jn4Oxalzwk3FbbU7gemZ5JoDIDsxbac38+mDsfDjlvQsKmkdWg02lxnZr8ZDree1TDs3vlRRrfiKmEWGWdG2mvkVzOaZo1ojtFkmto4NEJKSmMo59PAbJxKSRCm26ZoYMoX9U2/gPgjoRogHkelYHEg6kEIgQLHn+wEcHObADJUhEgkShYMmq2nBmIWnopC8mLTaJTUQIuEqCPRkBrUqXhua5EQQwhtNpWrFPrB4lEFEuoIMp9AhTa2OYihhL6MoBP2sXOxrKDmBBMKaUJlEzMpPUtI/qs2/g17MozavojNWqWNVg3DK514nxZXFnV5p7V2pYN2XU3mFqeBrOZH1M5ALG5DeN2P7U/j9P42Qh0mX09CZI0Qwi6fiVn+rUKnFT+ZA4aRFworzC/+ywqxkGffzWp8A1nhjccyfvKffbKVg3DfPVv6LyWsfzr1ZZLO/ccWH/LoF35Y8oIFCxYsmBP/AzjVsds3j1jsAAAAJXRFWHRkYXRlOmNyZWF0ZQAyMDE2LTA5LTE3VDE1OjIxOjI2KzA4OjAwf1sxlgAAACV0RVh0ZGF0ZTptb2RpZnkAMjAxNC0xMi0xMFQxNjoyNjozOCswODowMNG8FMEAAABNdEVYdHNvZnR3YXJlAEltYWdlTWFnaWNrIDcuMC4xLTYgUTE2IHg4Nl82NCAyMDE2LTA5LTE3IGh0dHA6Ly93d3cuaW1hZ2VtYWdpY2sub3Jn3dmlTgAAABh0RVh0VGh1bWI6OkRvY3VtZW50OjpQYWdlcwAxp/+7LwAAABh0RVh0VGh1bWI6OkltYWdlOjpIZWlnaHQAMTI4Q3xBgAAAABd0RVh0VGh1bWI6OkltYWdlOjpXaWR0aAAxMjjQjRHdAAAAGXRFWHRUaHVtYjo6TWltZXR5cGUAaW1hZ2UvcG5nP7JWTgAAABd0RVh0VGh1bWI6Ok1UaW1lADE0MTgxOTk5OTi4MO+2AAAAEnRFWHRUaHVtYjo6U2l6ZQAyLjQ5S0LZH4WRAAAAX3RFWHRUaHVtYjo6VVJJAGZpbGU6Ly8vaG9tZS93d3dyb290L3NpdGUvd3d3LmVhc3lpY29uLm5ldC9jZG4taW1nLmVhc3lpY29uLmNuL3NyYy8xMTgyMS8xMTgyMTU2LnBuZ7meKEwAAAAASUVORK5CYII=",
        /**
         * 刷新图片的base64编码
         */
        freshImgBase64:"data:image/jpg;base64,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",
        /**
         * 是否校验成功
         *
         * @returns {boolean}
         */
        isValid:function() {
            return this.status == "VALID";
        }
    };
    myCaptcha.fn.init.prototype = myCaptcha.fn;
    /**
     * 获取鼠标事件位置
     *
     * @param event
     * @returns {{x: (Number|number), y: (Number|number)}}
     */
    function getEventPos(event) {
        var e = event || window.event;
        var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
        var scrollY = document.documentElement.scrollTop || document.body.scrollTop;
        var x = e.pageX || e.clientX + scrollX;
        var y = e.pageY || e.clientY + scrollY;
        return {
            x:x,
            y:y
        };
    }
    /**
     * 获取obj DOM元素的位置
     *
     * @param obj
     * @returns {{x: number, y: number}}
     */
    function getDOMPos(obj) {
        var pos = {
            top:0,
            left:0
        };
        if (obj.offsetParent) {
            while (obj.offsetParent) {
                pos.top += obj.offsetTop;
                pos.left += obj.offsetLeft;
                obj = obj.offsetParent;
            }
        } else if (obj.x) {
            pos.left += obj.x;
        } else if (obj.x) {
            pos.top += obj.y;
        }
        return {
            x:pos.left,
            y:pos.top
        };
    }
    /**
     * md5加密字符串
     *
     * @param string
     * @returns {string}
     */
    function md5(string) {
        function md5_RotateLeft(lValue, iShiftBits) {
            return lValue << iShiftBits | lValue >>> 32 - iShiftBits;
        }
        function md5_AddUnsigned(lX, lY) {
            var lX4, lY4, lX8, lY8, lResult;
            lX8 = lX & 2147483648;
            lY8 = lY & 2147483648;
            lX4 = lX & 1073741824;
            lY4 = lY & 1073741824;
            lResult = (lX & 1073741823) + (lY & 1073741823);
            if (lX4 & lY4) {
                return lResult ^ 2147483648 ^ lX8 ^ lY8;
            }
            if (lX4 | lY4) {
                if (lResult & 1073741824) {
                    return lResult ^ 3221225472 ^ lX8 ^ lY8;
                } else {
                    return lResult ^ 1073741824 ^ lX8 ^ lY8;
                }
            } else {
                return lResult ^ lX8 ^ lY8;
            }
        }
        function md5_F(x, y, z) {
            return x & y | ~x & z;
        }
        function md5_G(x, y, z) {
            return x & z | y & ~z;
        }
        function md5_H(x, y, z) {
            return x ^ y ^ z;
        }
        function md5_I(x, y, z) {
            return y ^ (x | ~z);
        }
        function md5_FF(a, b, c, d, x, s, ac) {
            a = md5_AddUnsigned(a, md5_AddUnsigned(md5_AddUnsigned(md5_F(b, c, d), x), ac));
            return md5_AddUnsigned(md5_RotateLeft(a, s), b);
        }
        function md5_GG(a, b, c, d, x, s, ac) {
            a = md5_AddUnsigned(a, md5_AddUnsigned(md5_AddUnsigned(md5_G(b, c, d), x), ac));
            return md5_AddUnsigned(md5_RotateLeft(a, s), b);
        }
        function md5_HH(a, b, c, d, x, s, ac) {
            a = md5_AddUnsigned(a, md5_AddUnsigned(md5_AddUnsigned(md5_H(b, c, d), x), ac));
            return md5_AddUnsigned(md5_RotateLeft(a, s), b);
        }
        function md5_II(a, b, c, d, x, s, ac) {
            a = md5_AddUnsigned(a, md5_AddUnsigned(md5_AddUnsigned(md5_I(b, c, d), x), ac));
            return md5_AddUnsigned(md5_RotateLeft(a, s), b);
        }
        function md5_ConvertToWordArray(string) {
            var lWordCount;
            var lMessageLength = string.length;
            var lNumberOfWords_temp1 = lMessageLength + 8;
            var lNumberOfWords_temp2 = (lNumberOfWords_temp1 - lNumberOfWords_temp1 % 64) / 64;
            var lNumberOfWords = (lNumberOfWords_temp2 + 1) * 16;
            var lWordArray = Array(lNumberOfWords - 1);
            var lBytePosition = 0;
            var lByteCount = 0;
            while (lByteCount < lMessageLength) {
                lWordCount = (lByteCount - lByteCount % 4) / 4;
                lBytePosition = lByteCount % 4 * 8;
                lWordArray[lWordCount] = lWordArray[lWordCount] | string.charCodeAt(lByteCount) << lBytePosition;
                lByteCount++;
            }
            lWordCount = (lByteCount - lByteCount % 4) / 4;
            lBytePosition = lByteCount % 4 * 8;
            lWordArray[lWordCount] = lWordArray[lWordCount] | 128 << lBytePosition;
            lWordArray[lNumberOfWords - 2] = lMessageLength << 3;
            lWordArray[lNumberOfWords - 1] = lMessageLength >>> 29;
            return lWordArray;
        }
        function md5_WordToHex(lValue) {
            var WordToHexValue = "", WordToHexValue_temp = "", lByte, lCount;
            for (lCount = 0; lCount <= 3; lCount++) {
                lByte = lValue >>> lCount * 8 & 255;
                WordToHexValue_temp = "0" + lByte.toString(16);
                WordToHexValue = WordToHexValue + WordToHexValue_temp.substr(WordToHexValue_temp.length - 2, 2);
            }
            return WordToHexValue;
        }
        function md5_Utf8Encode(string) {
            string = string.replace(/\r\n/g, "\n");
            var utftext = "";
            for (var n = 0; n < string.length; n++) {
                var c = string.charCodeAt(n);
                if (c < 128) {
                    utftext += String.fromCharCode(c);
                } else if (c > 127 && c < 2048) {
                    utftext += String.fromCharCode(c >> 6 | 192);
                    utftext += String.fromCharCode(c & 63 | 128);
                } else {
                    utftext += String.fromCharCode(c >> 12 | 224);
                    utftext += String.fromCharCode(c >> 6 & 63 | 128);
                    utftext += String.fromCharCode(c & 63 | 128);
                }
            }
            return utftext;
        }
        var x = Array();
        var k, AA, BB, CC, DD, a, b, c, d;
        var S11 = 7, S12 = 12, S13 = 17, S14 = 22;
        var S21 = 5, S22 = 9, S23 = 14, S24 = 20;
        var S31 = 4, S32 = 11, S33 = 16, S34 = 23;
        var S41 = 6, S42 = 10, S43 = 15, S44 = 21;
        string = md5_Utf8Encode(string);
        x = md5_ConvertToWordArray(string);
        a = 1732584193;
        b = 4023233417;
        c = 2562383102;
        d = 271733878;
        for (k = 0; k < x.length; k += 16) {
            AA = a;
            BB = b;
            CC = c;
            DD = d;
            a = md5_FF(a, b, c, d, x[k + 0], S11, 3614090360);
            d = md5_FF(d, a, b, c, x[k + 1], S12, 3905402710);
            c = md5_FF(c, d, a, b, x[k + 2], S13, 606105819);
            b = md5_FF(b, c, d, a, x[k + 3], S14, 3250441966);
            a = md5_FF(a, b, c, d, x[k + 4], S11, 4118548399);
            d = md5_FF(d, a, b, c, x[k + 5], S12, 1200080426);
            c = md5_FF(c, d, a, b, x[k + 6], S13, 2821735955);
            b = md5_FF(b, c, d, a, x[k + 7], S14, 4249261313);
            a = md5_FF(a, b, c, d, x[k + 8], S11, 1770035416);
            d = md5_FF(d, a, b, c, x[k + 9], S12, 2336552879);
            c = md5_FF(c, d, a, b, x[k + 10], S13, 4294925233);
            b = md5_FF(b, c, d, a, x[k + 11], S14, 2304563134);
            a = md5_FF(a, b, c, d, x[k + 12], S11, 1804603682);
            d = md5_FF(d, a, b, c, x[k + 13], S12, 4254626195);
            c = md5_FF(c, d, a, b, x[k + 14], S13, 2792965006);
            b = md5_FF(b, c, d, a, x[k + 15], S14, 1236535329);
            a = md5_GG(a, b, c, d, x[k + 1], S21, 4129170786);
            d = md5_GG(d, a, b, c, x[k + 6], S22, 3225465664);
            c = md5_GG(c, d, a, b, x[k + 11], S23, 643717713);
            b = md5_GG(b, c, d, a, x[k + 0], S24, 3921069994);
            a = md5_GG(a, b, c, d, x[k + 5], S21, 3593408605);
            d = md5_GG(d, a, b, c, x[k + 10], S22, 38016083);
            c = md5_GG(c, d, a, b, x[k + 15], S23, 3634488961);
            b = md5_GG(b, c, d, a, x[k + 4], S24, 3889429448);
            a = md5_GG(a, b, c, d, x[k + 9], S21, 568446438);
            d = md5_GG(d, a, b, c, x[k + 14], S22, 3275163606);
            c = md5_GG(c, d, a, b, x[k + 3], S23, 4107603335);
            b = md5_GG(b, c, d, a, x[k + 8], S24, 1163531501);
            a = md5_GG(a, b, c, d, x[k + 13], S21, 2850285829);
            d = md5_GG(d, a, b, c, x[k + 2], S22, 4243563512);
            c = md5_GG(c, d, a, b, x[k + 7], S23, 1735328473);
            b = md5_GG(b, c, d, a, x[k + 12], S24, 2368359562);
            a = md5_HH(a, b, c, d, x[k + 5], S31, 4294588738);
            d = md5_HH(d, a, b, c, x[k + 8], S32, 2272392833);
            c = md5_HH(c, d, a, b, x[k + 11], S33, 1839030562);
            b = md5_HH(b, c, d, a, x[k + 14], S34, 4259657740);
            a = md5_HH(a, b, c, d, x[k + 1], S31, 2763975236);
            d = md5_HH(d, a, b, c, x[k + 4], S32, 1272893353);
            c = md5_HH(c, d, a, b, x[k + 7], S33, 4139469664);
            b = md5_HH(b, c, d, a, x[k + 10], S34, 3200236656);
            a = md5_HH(a, b, c, d, x[k + 13], S31, 681279174);
            d = md5_HH(d, a, b, c, x[k + 0], S32, 3936430074);
            c = md5_HH(c, d, a, b, x[k + 3], S33, 3572445317);
            b = md5_HH(b, c, d, a, x[k + 6], S34, 76029189);
            a = md5_HH(a, b, c, d, x[k + 9], S31, 3654602809);
            d = md5_HH(d, a, b, c, x[k + 12], S32, 3873151461);
            c = md5_HH(c, d, a, b, x[k + 15], S33, 530742520);
            b = md5_HH(b, c, d, a, x[k + 2], S34, 3299628645);
            a = md5_II(a, b, c, d, x[k + 0], S41, 4096336452);
            d = md5_II(d, a, b, c, x[k + 7], S42, 1126891415);
            c = md5_II(c, d, a, b, x[k + 14], S43, 2878612391);
            b = md5_II(b, c, d, a, x[k + 5], S44, 4237533241);
            a = md5_II(a, b, c, d, x[k + 12], S41, 1700485571);
            d = md5_II(d, a, b, c, x[k + 3], S42, 2399980690);
            c = md5_II(c, d, a, b, x[k + 10], S43, 4293915773);
            b = md5_II(b, c, d, a, x[k + 1], S44, 2240044497);
            a = md5_II(a, b, c, d, x[k + 8], S41, 1873313359);
            d = md5_II(d, a, b, c, x[k + 15], S42, 4264355552);
            c = md5_II(c, d, a, b, x[k + 6], S43, 2734768916);
            b = md5_II(b, c, d, a, x[k + 13], S44, 1309151649);
            a = md5_II(a, b, c, d, x[k + 4], S41, 4149444226);
            d = md5_II(d, a, b, c, x[k + 11], S42, 3174756917);
            c = md5_II(c, d, a, b, x[k + 2], S43, 718787259);
            b = md5_II(b, c, d, a, x[k + 9], S44, 3951481745);
            a = md5_AddUnsigned(a, AA);
            b = md5_AddUnsigned(b, BB);
            c = md5_AddUnsigned(c, CC);
            d = md5_AddUnsigned(d, DD);
        }
        return (md5_WordToHex(a) + md5_WordToHex(b) + md5_WordToHex(c) + md5_WordToHex(d)).toLowerCase();
    }
    // 导出myCaptcha为全局对象
    window.myCaptcha = myCaptcha;
})(window);