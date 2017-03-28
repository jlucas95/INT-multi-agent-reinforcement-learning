package policy;

import main.Action;
import main.State;

import java.io.Serializable;
import java.util.HashMap;

public interface Policy{
	Action getAction(State state);
}
