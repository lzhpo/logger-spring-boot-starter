/*
 * Copyright lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzhpo.logger;

import com.lzhpo.logger.domain.OrderRequest;
import com.lzhpo.logger.domain.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author lzhpo
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class LoggerActionEndpointTest {

    private final LoggerAction loggerAction;

    @PutMapping("/updateAddress")
    public void updateAddress(@RequestParam String oldAddress, @RequestParam String newAddress) {
        loggerAction.updateAddress(oldAddress, newAddress);
    }

    @PutMapping("/updateNewAddress")
    public void updateNewAddress(@RequestParam String newAddress) {
        loggerAction.updateNewAddress(newAddress);
    }

    @PutMapping("/updateAddressDiff")
    public void updateAddressDiff(@RequestParam String oldAddress, @RequestParam String newAddress) {
        loggerAction.updateAddressDiff(oldAddress, newAddress);
    }

    @PutMapping("/updateNewSex")
    public void updateNewSex(@RequestParam String newSex) {
        loggerAction.updateNewSex(newSex);
    }

    @GetMapping("/findUserAge")
    public Integer findUserAge(@RequestParam String userId) {
        return loggerAction.findUserAge(userId);
    }

    @GetMapping("/systemDate")
    public void systemDate() {
        loggerAction.systemDate();
    }

    @PostMapping("/createOrder1")
    public OrderResponse createOrder1(@RequestBody OrderRequest request) {
        return loggerAction.createOrder1(request);
    }

    @PostMapping("/createOrder2")
    public OrderResponse createOrder2(@RequestBody OrderRequest request) {
        return loggerAction.createOrder2(request);
    }
}
