package com.sharex.token.api.controller;

import com.sharex.token.api.entity.RESTful;
import com.sharex.token.api.service.CurrencyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("交易所数据接口")
@RequestMapping("/currency")
@RestController
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @ApiOperation("单币聚合")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "exchangeName", value = "交易所", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "currency", value = "币种", required = true)
    })
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public RESTful get(@RequestHeader String token, String exchangeName, String currency) {

        return currencyService.get(token, exchangeName, currency);
    }

    @ApiOperation("行情")
    @RequestMapping(value = "/getTicker", method = RequestMethod.GET)
    public RESTful getTicker(String exchangeName, String symbol) {

        return currencyService.getTicker(exchangeName, symbol);
    }

    @ApiOperation("k线")
    @RequestMapping(value = "/getKline", method = RequestMethod.GET)
    public RESTful getKline(String exchangeName, String symbol, String type) {

        return currencyService.getKline(exchangeName, symbol, type);
    }

    @ApiOperation("最新成交")
    @RequestMapping(value = "/getTrades", method = RequestMethod.GET)
    public RESTful getTrades(String exchangeName, String symbol, String direction) {

        return currencyService.getTrades(exchangeName, symbol, direction);
    }
}
