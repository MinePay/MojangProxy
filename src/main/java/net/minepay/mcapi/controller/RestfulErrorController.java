package net.minepay.mcapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

/**
 * Provides an exclusively restful error controller.
 *
 * @author <a href="mailto:johannesd@torchmind.com">Johannes Donath</a>
 */
@RestController
@RequestMapping("/error")
public class RestfulErrorController extends AbstractErrorController {

    @Autowired
    public RestfulErrorController(@Nonnull ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    /**
     * Handles failed requests.
     *
     * @param request a request.
     * @return an error response.
     */
    @ResponseBody
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = this.getErrorAttributes(request, false);
        HttpStatus status = this.getStatus(request);

        return new ResponseEntity<>(body, status);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getErrorPath() {
        return "/error";
    }
}
