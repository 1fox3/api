package com.fox.api.service.stock;

import java.util.List;
import java.util.Map;

public interface StockFollowService {

    List<Map<String, Object>> getByUser(int userId);
}
