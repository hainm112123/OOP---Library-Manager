package org.example.librarymanager.data;

import com.github.scribejava.core.oauth.OAuth20Service;
import org.example.librarymanager.Common;
import org.example.librarymanager.services.GoogleOAuth2;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

public class GoogleOAuth2Test {
    @Test
    public void test() {
//        Scanner scanner = new Scanner(System.in);
//        OAuth20Service service = GoogleOAuth2.getInstance().getService();
//        String authorizeUrl = service.getAuthorizationUrl();
//        System.out.println("auth url: " + authorizeUrl);
//        System.out.println("authorize code: ");
        String code = "4%2F0AeanS0ZKnp_y0t5F-CY45c4Ust9x7KwMPaSkk7phwXL12AozqU1sb-S6b0UEGoKZ_WM4Zw";
        System.out.println(GoogleOAuth2.getInstance().handleCallback(code));
    }

    @Test
    public void testHashEmail() {
        String email = "test@example.com";
        System.out.println(Common.hashString(email, 10));
    }
}
