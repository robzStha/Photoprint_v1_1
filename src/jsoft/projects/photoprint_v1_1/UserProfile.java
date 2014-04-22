package jsoft.projects.photoprint_v1_1;

import jsoft.projects.photoprint_v1_1.libs.SessionMngr;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class UserProfile extends Fragment{

	EditText etName, etUsername, etEmail, etContact, etCountry, etState, etCity, etMyself;
	
	SessionMngr session;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = new SessionMngr(getActivity());
		View rootView = inflater.inflate(R.layout.user_profile,container,false);
		
		etName = (EditText) rootView.findViewById(R.id.etName);
		etUsername = (EditText) rootView.findViewById(R.id.etUsername);
		etEmail = (EditText) rootView.findViewById(R.id.etEmail);
		etContact = (EditText) rootView.findViewById(R.id.etContact);
		etCountry = (EditText) rootView.findViewById(R.id.etCountry);
		etState = (EditText) rootView.findViewById(R.id.etState);
		etCity = (EditText) rootView.findViewById(R.id.etCity);
		etMyself = (EditText) rootView.findViewById(R.id.etMyself);
		
		etName.setText(session.getStringValues("userName"));
		etUsername.setText(session.getStringValues("username"));
		etEmail.setText(session.getStringValues("email"));
		etContact.setText(session.getStringValues("contact"));
		etCountry.setText(session.getStringValues("country"));
		etState.setText(session.getStringValues("state"));
		etCity.setText(session.getStringValues("city"));
		etMyself.setText(session.getStringValues("myself"));
		
		return rootView;
	}
	
}
