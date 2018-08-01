package com.sharex.token.api.currency.resolver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharex.token.api.currency.ApiClinetFactory;
import com.sharex.token.api.currency.IApiClient;
import com.sharex.token.api.currency.huobi.HuoBiApiClient;
import com.sharex.token.api.currency.huobi.resp.ApiResp;
import com.sharex.token.api.currency.huobi.resp.Kline;
import com.sharex.token.api.currency.huobi.resp.Trade;
import com.sharex.token.api.currency.huobi.resp.Trades;
import com.sharex.token.api.entity.MyKline;
import com.sharex.token.api.entity.MyTrade;
import com.sharex.token.api.entity.MyTrades;
import com.sharex.token.api.entity.RemoteSyn;
import com.sharex.token.api.exception.KlineSynException;
import com.sharex.token.api.exception.NetworkException;
import com.sharex.token.api.exception.TradesSynException;
import com.sharex.token.api.service.RemoteSynService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedList;
import java.util.List;

@Configuration
public class HuoBiApiResolver implements IApiResolver {

    private static final Log logger = LogFactory.getLog(RemoteSynService.class);

    private IApiClient apiClient = new HuoBiApiClient();

    private ObjectMapper objectMapper = new ObjectMapper();

    public RemoteSyn getKline(String symbol, String type) throws Exception {

        //
        IApiClient apiClient = ApiClinetFactory.getInstence("huobi");

        // 同步redis，判断时间戳时间 --> 150条
        String respBody = apiClient.kline(symbol, type, 150);

        if (logger.isDebugEnabled()) {
            logger.debug(respBody);
        }

        // 解析
        if (!StringUtils.isBlank(respBody)) {
            ApiResp apiResp = objectMapper.readValue(respBody, ApiResp.class);
            if ("ok".equals(apiResp.getStatus())) {

                RemoteSyn<List<MyKline>> remoteSyn = new RemoteSyn<>();

                remoteSyn.setTs(apiResp.getTs());

                // myKlineList
                List<MyKline> myKlineList = new LinkedList<>();

                List<Kline> klineList = objectMapper.convertValue(apiResp.getData(), new TypeReference<List<Kline>>() { });
                Integer count = klineList.size();
                for (int i = 0; i < count; i++) {
                    Kline kline = klineList.get(i);
                    MyKline myKline = new MyKline();
                    myKline.setId(kline.getId());
                    myKline.setOpen(kline.getOpen().toString());
                    myKline.setHigh(kline.getHigh().toString());
                    myKline.setLow(kline.getLow().toString());
                    myKline.setClose(kline.getClose().toString());
                    myKline.setAmount(kline.getAmount().toString());
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

            throw new KlineSynException(apiResp.getErrMsg());
        }

        throw new NetworkException();
    }

    public RemoteSyn getTrades(String symbol) throws Exception {

        IApiClient apiClient = ApiClinetFactory.getInstence("huobi");

        // 分析数据 100条内 肯定存在 大于 10条的买 和 大于 10条的卖
        String respBody = apiClient.trades(symbol, 100);

        if (logger.isDebugEnabled()) {
            logger.debug(respBody);
        }

        if (!StringUtils.isBlank(respBody)) {
            ApiResp apiResp = objectMapper.readValue(respBody, ApiResp.class);
            if ("ok".equals(apiResp.getStatus())) {

                RemoteSyn<MyTrades> remoteSyn = new RemoteSyn<>();

                remoteSyn.setTs(apiResp.getTs());

                MyTrades myTrades = new MyTrades();

                // myKlineList
                List<MyTrade> tradeList_buy = new LinkedList<>();
                List<MyTrade> tradeList_sell = new LinkedList<>();

                List<Trades> tradesList = objectMapper.convertValue(apiResp.getData(), new TypeReference<List<Trades>>() { });
                for (Trades trades:tradesList) {
                    List<Trade> tradeList = trades.getData();
                    for (Trade trade:tradeList) {
                        if ("buy".equals(trade.getDirection())) {
                            MyTrade myTrade = new MyTrade();
                            myTrade.setId(trade.getId());
                            myTrade.setTs(trade.getTs());
                            myTrade.setPrice(trade.getPrice());
                            myTrade.setAmount(trade.getAmount());
                            myTrade.setDirection(trade.getDirection());
                            tradeList_buy.add(myTrade);
                        } else if ("sell".equals(trade.getDirection())) {
                            MyTrade myTrade = new MyTrade();
                            myTrade.setId(trade.getId());
                            myTrade.setTs(trade.getTs());
                            myTrade.setPrice(trade.getPrice());
                            myTrade.setAmount(trade.getAmount());
                            myTrade.setDirection(trade.getDirection());
                            tradeList_sell.add(myTrade);
                        }
                    }
                }

                myTrades.setBuy(tradeList_buy);
                myTrades.setSell(tradeList_sell);

                remoteSyn.setData(myTrades);

                return remoteSyn;
            }

            throw new TradesSynException(apiResp.getErrMsg());
        }

        throw new NetworkException();
    }
}