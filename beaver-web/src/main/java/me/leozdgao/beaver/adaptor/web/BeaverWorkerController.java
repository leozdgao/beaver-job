package me.leozdgao.beaver.adaptor.web;

import me.leozdgao.beaver.client.Response;
import me.leozdgao.beaver.worker.Worker;
import me.leozdgao.beaver.worker.sd.ServiceDiscovery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Worker 相关控制器
 * @author leozdgao
 */
@RequestMapping("api/v1/workers")
@RestController
public class BeaverWorkerController {
    @Resource
    private ServiceDiscovery serviceDiscovery;

    @GetMapping("getCurrentWorkers")
    public Response<List<Worker>> getCurrentWorkers(String scope) {
        if (scope == null) {
            scope = "DEFAULT";
        }
        List<Worker> workers = serviceDiscovery.getCurrentWorkers(scope);
        return Response.buildSuccess(workers);
    }
}
