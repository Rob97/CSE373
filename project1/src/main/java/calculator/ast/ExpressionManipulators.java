package calculator.ast;

import calculator.interpreter.Environment;


import calculator.errors.EvaluationError;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;


/**
 * All of the static methods in this class are given the exact same parameters for
 * consistency. You can often ignore some of these parameters when implementing your
 * methods.
 *
 * Some of these methods should be recursive. You may want to consider using public-private
 * pairs in some cases.
 */
public class ExpressionManipulators {
    /**
     * Accepts an 'toDouble(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'toDouble'.
     * - The 'node' parameter has exactly one child: the AstNode to convert into a double.
     *
     * Postconditions:
     *
     * - Returns a number AstNode containing the computed double.
     *
     * For example, if this method receives the AstNode corresponding to
     * 'toDouble(3 + 4)', this method should return the AstNode corresponding
     * to '7'.
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if any of the expressions uses an unknown operation.
     */
    public static AstNode handleToDouble(Environment env, AstNode node) {
        // To help you get started, we've implemented this method for you.
        // You should fill in the TDOs in the 'toDoubleHelper' method.
        return new AstNode(toDoubleHelper(env.getVariables(), node.getChildren().get(0)));
    }

    private static double toDoubleHelper(IDictionary<String, AstNode> variables, AstNode node) {
        // There are three types of nodes, so we have three cases.
        if (node.isNumber()) {
        		// This is a leaf node
        		// Return the double form of this constant value
        		return node.getNumericValue();
        		
        } else if (node.isVariable()) {
        		// This is a leaf node
        		if (!variables.containsKey(node.getName())) {
        			throw new EvaluationError("Undefined Variable: " + node.getName());
        		} else {
        			return toDoubleHelper(variables, variables.get(node.getName()));
        		}
        } else {
			String name = node.getName();
    			IList<AstNode> children = node.getChildren();
    			switch(name) {
				case "+":
					return toDoubleHelper(variables, children.get(0)) + toDoubleHelper(variables, children.get(1));
				case "-":
					return toDoubleHelper(variables, children.get(0)) - toDoubleHelper(variables, children.get(1));
				case "*":
					return toDoubleHelper(variables, children.get(0))  * toDoubleHelper(variables, children.get(1));
				case "/":
					return toDoubleHelper(variables, children.get(0)) / toDoubleHelper(variables, children.get(1));
				case "negate":
					return toDoubleHelper(variables, children.get(0)) * -1.0;
				case "sin":
					return Math.sin(toDoubleHelper(variables, children.get(0)));
				case "cos":
					return Math.cos(toDoubleHelper(variables, children.get(0)));
				case "^":
					return Math.pow(toDoubleHelper(variables, children.get(0)), toDoubleHelper(variables, children.get(1)));
				default:
					throw new EvaluationError("Unsupported Operation " + name);
			}
        }
    }
    

    /**
     * Accepts a 'simplify(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'simplify'.
     * - The 'node' parameter has exactly one child: the AstNode to simplify
     *
     * Postconditions:
     *
     * - Returns an AstNode containing the simplified inner parameter.
     *
     * For example, if we received the AstNode corresponding to the expression
     * "simplify(3 + 4)", you would return the AstNode corresponding to the
     * number "7".
     *
     * Note: there are many possible simplifications we could implement here,
     * but you are only required to implement a single one: constant folding.
     *
     * That is, whenever you see expressions of the form "NUM + NUM", or
     * "NUM - NUM", or "NUM * NUM", simplify them.
     */
    public static AstNode handleSimplify(Environment env, AstNode node) {
    		// Start by first trying to simplify all variables
    		// Then switch to actual evaluation
    		node.getChildren().set(0, simplifyVariables(env, node.getChildren().get(0)));
    		node.getChildren().set(0, simplifyHelper(env, node.getChildren().get(0)));
    		return node.getChildren().get(0);
    }
    
    private static AstNode simplifyHelper(Environment env, AstNode node) {
    		if (node.isOperation()) {
        		// If the node is an operation, we need to check it's type
        		String name = node.getName();
        		IList<AstNode> children = node.getChildren();
        		if (name.equals("-") || name.equals("+") || name.equals("*")) {
        				if (children.get(0).isNumber() && children.get(1).isNumber()) {
        					return new AstNode(toDoubleHelper(env.getVariables(), node));
        				} else {
                			for (int i = 0; i < children.size(); i++) {
                				children.set(i, simplifyHelper(env, children.get(i)));
                			}
                			return node;
        				}
        		} else {
        			for (int i = 0; i < children.size(); i++) {
        				children.set(i, simplifyHelper(env, children.get(i)));
        			}
        			return node;
        		}
        } else {
        		return node;
        }
    	
    		
    }
    
    /**
     * 	Iterates through the given tree and simply tries to evaluate each variable
     *  into a mathematical expression if the variable has been defined already
     */
    private static AstNode simplifyVariables(Environment env, AstNode node) {
		IList<AstNode> children = node.getChildren();	
    		if (node.isNumber()) {
    			return node;
    		} else if (node.isOperation()) {
    			for (int i = 0; i < children.size(); i++) {
				children.set(i, simplifyVariables(env, children.get(i)));
			}
			return node;
    		} else {
    			if (env.getVariables().containsKey(node.getName())) {
    				AstNode var = env.getVariables().get(node.getName());
    				IList<AstNode> varChildren = var.getChildren();
    				
    				AstNode newNode;
    				IList<AstNode> newChildren = new DoubleLinkedList<AstNode>();
    				if (var.isOperation()) {
    					newNode = new AstNode(var.getName(), newChildren);
    				} else if (var.isNumber()) {
    					newNode = new AstNode(var.getNumericValue());
    				} else {
    					newNode = new AstNode(var.getName());
    				}
    				
    				
    				for (int i = 0; i < varChildren.size(); i++) {
    					if (varChildren.get(i).isNumber()) {
    						newChildren.add(simplifyVariables(env, new AstNode(varChildren.get(i).getNumericValue())));
    					} else if (varChildren.get(i).isOperation()) {
    						newChildren.add(simplifyVariables(env, new AstNode(varChildren.get(i).getName(), varChildren.get(i).getChildren())));
    					} else {
    						newChildren.add(simplifyVariables(env, new AstNode(varChildren.get(i).getName())));
    					}
    				}
    				
    				return newNode;
    			} else {
    				return node;
    			}
    		}
    }

    /**
     * Accepts a 'plot(exprToPlot, var, varMin, varMax, step)' AstNode and
     * generates the corresponding plot. Returns some arbitrary AstNode.
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This method will receive the AstNode corresponding to 'plot(3 * x, x, 2, 5, 0.5)'.
     * Your 'handlePlot' method is then responsible for plotting the equation
     * "3 * x", varying "x" from 2 to 5 in increments of 0.5.
     *
     * In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'var' was already defined
     * @throws EvaluationError  if 'step' is zero or negative
     */
    public static AstNode plot(Environment env, AstNode node) {

        IDictionary<String, AstNode> variables = env.getVariables();
        AstNode expression = node.getChildren().get(0);
        
        // get plotting variable
        String varName = node.getChildren().get(1).getName();
        
        // resolve input to doubles
        Double varMin = toDoubleHelper(variables, node.getChildren().get(2));
        Double varMax = toDoubleHelper(variables, node.getChildren().get(3));
        Double varStep = toDoubleHelper(variables, node.getChildren().get(4));
        
        // check for evaluation error cases
        
        if (varMin > varMax) {
            throw new EvaluationError("varMin > varMax");
        }
        
        if (varStep <= 0) {
            throw new EvaluationError("varStep <= 0");
        }
        
        if (variables.containsKey(varName)) {
            throw new EvaluationError("var '" + varName + "' already defined");
        }
        
        // create the values for plotting
        IList<Double> xValues = new DoubleLinkedList<Double>();
        IList<Double> yValues = new DoubleLinkedList<Double>();
        
        for (; varMin <= varMax; varMin += varStep) {
            variables.put(varName, new AstNode(varMin));
            xValues.add(varMin);
            yValues.add(toDoubleHelper(variables, expression)); 
        }
        
        // remove the varName from the variables list now
        if (variables.containsKey(varName)) {
            variables.remove(varName);
        }
        
        // draw the plot on a panel
        env.getImageDrawer().drawScatterPlot("Plot", varName, "Output", xValues, yValues);
        
        
        //throw new NotYetImplementedException();

        // Note: every single function we add MUST return an
        // AST node that your "simplify" function is capable of handling.
        // However, your "simplify" function doesn't really know what to do
        // with "plot" functions (and what is the "plot" function supposed to
        // evaluate to anyways?) so we'll settle for just returning an
        // arbitrary number.
        //
        // When working on this method, you should uncomment the following line:
        //
        return new AstNode(1);
    }
}
