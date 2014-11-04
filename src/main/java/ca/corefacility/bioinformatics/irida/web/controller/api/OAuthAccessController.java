package ca.corefacility.bioinformatics.irida.web.controller.api;

import java.security.Principal;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Joiner;

/**
 * Controller class for serving custom OAuth2 confirmation pages
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
@Controller
@SessionAttributes("authorizationRequest")
public class OAuthAccessController {
	private static final Logger logger = LoggerFactory.getLogger(OAuthAccessController.class);

	@Autowired
	private ClientDetailsService clientDetailsService;
	
	/**
	 * Basic access confirmation controller for OAuth2
	 * @param model Model objects to be passed to the view
	 * @param principal The principal user making the auth request
	 * @return A ModelAndView for the access_confirmation page
	 */
	@RequestMapping("/oauth/confirm_access")
	public ModelAndView getAccessConfirmation(Map<String, Object> model, Principal principal) {
		//get the authorization request from the model
		AuthorizationRequest clientAuth = (AuthorizationRequest) model.remove("authorizationRequest");
		
		logger.trace("Token request recieved from " + clientAuth.getClientId() + " for " + principal.getName());
		//get a list of the scopes from the request
		Set<String> scopes = clientAuth.getScope();
		String join = Joiner.on(" & ").join(scopes);
		
		//add necessary information to the model
		model.put("auth_request", clientAuth);
		model.put("scopes",join);
		model.put("principal", principal);

		return new ModelAndView("access_confirmation", model);
	}
}