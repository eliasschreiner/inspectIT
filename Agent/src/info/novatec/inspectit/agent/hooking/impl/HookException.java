<<<<<<< HEAD
package info.novatec.inspectit.agent.hooking.impl;

/**
 * This exception is thrown when a something bad happened at hooking a class/method.
 * 
 * @author Patrice Bouillet
 */
public class HookException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 1043321043946792608L;

	/**
	 * Default constructor which takes one argument used for a message of the exception.
	 * 
	 * @param msg
	 *            The message.
	 */
	public HookException(String msg) {
		super(msg);
	}

	/**
	 * Additional constructor which can store the origin exception.
	 * 
	 * @param msg
	 *            The message.
	 * @param throwable
	 *            The origin exception.
	 */
	public HookException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
=======
package info.novatec.inspectit.agent.hooking.impl;

/**
 * This exception is thrown when a something bad happened at hooking a class/method.
 * 
 * @author Patrice Bouillet
 */
public class HookException extends Exception {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 1043321043946792608L;

	/**
	 * Default constructor which takes one argument used for a message of the exception.
	 * 
	 * @param msg
	 *            The message.
	 */
	public HookException(String msg) {
		super(msg);
	}

	/**
	 * Additional constructor which can store the origin exception.
	 * 
	 * @param msg
	 *            The message.
	 * @param throwable
	 *            The origin exception.
	 */
	public HookException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
}
>>>>>>> 05dea9942f336a2ce370b3f3e5f86539fa1f767c
