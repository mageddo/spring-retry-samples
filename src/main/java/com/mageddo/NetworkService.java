package com.mageddo;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 * @author elvis
 * @version $Revision: $<br/>
 *          $Id: $
 * @since 6/20/17 9:59 AM
 */
@Component
public class NetworkService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Cacheable("teste")
    @Retryable(include = RuntimeException.class, backoff = @Backoff(delay = 5000, multiplier = 2), maxAttempts = 5)
    public String ping(String hostname){
        if(new Random().nextBoolean()){
            logger.info("status=suc");
            return ":)";
        }
        logger.info("status=err");
        throw new RuntimeException(":(");
    }

    /**
     * Se esse bean for uma interface, a anotacao  PRECISA ser declarada nela (soh a @Recover)
     * @param e
     * @param hostname
     * @return
     */
    @Recover
    public String recover(RuntimeException e, String hostname){
        logger.error("status=fat");
        return ":|";
    }

    public String resolveName(final String hostname) {

        final RetryTemplate retryTemplate = new RetryTemplate();

        final ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setMaxInterval(1000);
        policy.setMultiplier(2);
        retryTemplate.setBackOffPolicy(policy);

        final SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(2);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate.execute(new RetryCallback<String, RuntimeException>() {
            @Override
            public String doWithRetry(RetryContext context) throws RuntimeException {

                if(new Random().nextBoolean()) {
                    logger.info("status=suc");
                    return "192.168.0.1";
                }else {
                    logger.error("status=err");
                    throw new RuntimeException(String.format("name %s not found", hostname));
                }

            }
        }, new RecoveryCallback<String>() {
            @Override
            public String recover(RetryContext context) throws Exception {
                logger.info("status=recovered");
                return "127.0.0.1";
            }
        });
    }
}

