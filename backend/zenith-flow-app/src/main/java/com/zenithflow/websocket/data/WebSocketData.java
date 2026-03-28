

package com.zenithflow.websocket.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.websocket.Session;

/**
 * WebSocket连接数据
 *
 *
 */
@Data
@AllArgsConstructor
public class WebSocketData {
    private Long userId;
    private Session session;
}
