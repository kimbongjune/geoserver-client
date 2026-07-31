package com.example.geoserverexample.common;

import io.github.kimbongjune.geoserverclient.exception.GeoServerException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Centralized handling for {@link GeoServerException} so individual controllers don't each need
 * their own try/catch around every service call.
 *
 * <p>Redirecting back to the {@code Referer} is safe here because every controller in this app
 * redirects to the same target on both success and failure of a given action — there's no
 * per-method branching to reproduce, so "wherever the user submitted the form from" is exactly
 * the page they'd otherwise have been redirected back to anyway.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeoServerException.class)
    public String handleGeoServerException(GeoServerException e, HttpServletRequest request, RedirectAttributes redirect) {
        redirect.addFlashAttribute("err", e.getClass().getSimpleName() + ": " + e.getMessage());
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}
