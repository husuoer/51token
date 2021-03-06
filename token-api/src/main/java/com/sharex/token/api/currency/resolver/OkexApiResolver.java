package com.sharex.token.api.currency.resolver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharex.token.api.currency.okex.OkexApiClient;
import com.sharex.token.api.currency.okex.resp.Trade;
import com.sharex.token.api.entity.*;
import com.sharex.token.api.exception.NetworkException;
import com.sharex.token.api.exception.TradesSynException;
import com.sharex.token.api.service.RemoteSynService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class OkexApiResolver implements IApiResolver {

    private static final Log logger = LogFactory.getLog(RemoteSynService.class);

//    private IApiClient apiClient = new OkexApiClient();

    @Autowired
    private OkexApiClient okexApiClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public RemoteSyn getKline(String symbol, String type) throws Exception {

        // 同步redis，判断时间戳时间 --> 150条
        String respBody = okexApiClient.kline(symbol, type, 150);

        if (logger.isDebugEnabled()) {
            logger.debug(respBody);
        }

        // 解析
        if (!StringUtils.isBlank(respBody)) {
            // 二维数组

            RemoteSyn<List<MyKline>> remoteSyn = new RemoteSyn<>();

            // myKlineList
            List<MyKline> myKlineList = new LinkedList<>();

            List<List<String>> klineList = objectMapper.readValue(respBody, new TypeReference<List<List<String>>>() {});
            Integer count = klineList.size();
            for (int i = 0; i < count; i++) {
                List<String> klineFields = klineList.get(i);
                MyKline myKline = new MyKline();
                myKline.setId(Long.valueOf(klineFields.get(0)));
                myKline.setOpen(klineFields.get(1));
                myKline.setHigh(klineFields.get(2));
                myKline.setLow(klineFields.get(3));
                myKline.setClose(klineFields.get(4));
                myKline.setAmount(klineFields.get(5));
                myKlineList.add(myKline);
            }
            remoteSyn.setData(myKlineList);

            // huobi
            //   kline_symbol_type(例: huobi_btcusdt_1min)
            // 存储 redis格式
            // {
            //    Long ts: 1533095400000
            //    Object data: [{}, {}]
            // }

            return remoteSyn;
        }

        throw new NetworkException();
    }

    @Override
    public RemoteSyn getTrades(String symbol) throws Exception {

        String respBody = okexApiClient.trades(symbol, 100);

        if (logger.isDebugEnabled()) {
            logger.debug(respBody);
        }

        // 解析
        if (!StringUtils.isBlank(respBody)) {

            RemoteSyn<MyTrades> remoteSyn = new RemoteSyn<>();

            MyTrades myTrades = new MyTrades();

            // myKlineList
            List<MyTrade> tradeList_buy = new LinkedList<>();
            List<MyTrade> tradeList_sell = new LinkedList<>();

            List<Trade> tradeList = objectMapper.readValue(respBody, new TypeReference<List<Trade>>() { });
            Integer count = tradeList.size();
            for (int i = 0; i < 100; i++) {
                Trade trade = tradeList.get(i);
                if ("buy".equals(trade.getType())) {
                    MyTrade myTrade = new MyTrade();
                    myTrade.setId(trade.getTid());
                    myTrade.setTs(trade.getDateMs());
                    myTrade.setPrice(trade.getPrice());
                    myTrade.setAmount(trade.getAmount());
                    myTrade.setDirection(trade.getType());
                    tradeList_buy.add(myTrade);
                } else if ("sell".equals(trade.getType())) {
                    MyTrade myTrade = new MyTrade();
                    myTrade.setId(trade.getTid());
                    myTrade.setTs(trade.getDateMs());
                    myTrade.setPrice(trade.getPrice());
                    myTrade.setAmount(trade.getAmount());
                    myTrade.setDirection(trade.getType());
                    tradeList_sell.add(myTrade);
                }
                myTrades.setBuy(tradeList_buy);
                myTrades.setSell(tradeList_sell);

                remoteSyn.setData(myTrades);

                return remoteSyn;
            }

            throw new TradesSynException("request okex server error");
        }

        throw new NetworkException();
    }

    @Override
    public Boolean accounts(String apiKey, String apiSecret) throws Exception {
        throw new RuntimeException();
    }

    @Override
    public Map<String, UserCurrency> accounts(String apiKey, String apiSecret, Integer userId) throws Exception {

        String respBody = okexApiClient.accounts(apiKey, apiSecret);

        if (logger.isDebugEnabled()) {
            logger.debug(respBody);
        }

        if (!StringUtils.isBlank(respBody)) {

            //{
            //    "result": true,
            //    "info": {
            //        "funds": {
            //            "free": {
            //                "ssc": "0",
            //                "okb": "0",
            //                ...
            //            },
            //            "freezed": {
            //                "ssc": "0",
            //                "okb": "0",
            //                ...
            //            }
            //        }
            //    }
            //}


        }

        throw new NetworkException();
    }

    @Override
    public RemotePost<String> placeOrder(String apiKey, String apiSecret, String accountId, String symbol, String price, String amount, String type) throws Exception {
        throw new RuntimeException();
    }

    @Override
    public RemotePost<String> cancelOrder(String apiKey, String apiSecret, String symbol, String orderId) throws Exception {
        throw new RuntimeException();
    }

    @Override
    public RemoteSyn getOpenOrders(String apiKey, String apiSecret, String accountId, String symbol, Integer status, Integer size) throws Exception {
        throw new RuntimeException();
    }

    @Override
    public RemoteSyn getHistoryOrders(String apiKey, String apiSecret, String accountId, String symbol, Integer status, Integer size) throws Exception {
        throw new RuntimeException();
    }
}
