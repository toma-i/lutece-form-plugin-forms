/*
 * Copyright (c) 2002-2018, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.FormDisplay;
import fr.paris.lutece.plugins.forms.business.FormDisplayHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;

/**
 * 
 * Management class for the display tree of a Step
 *
 */
public class StepDisplayTree
{
    private static final String STEP_TEMPLATE = "/skin/plugins/forms/composite_template/view_step.html";
    private static final String STEP_TEMPLATE_READ_ONLY = "/admin/plugins/forms/composite/view_step.html";
    private static final String STEP_TITLE_MARKER = "stepTitle";
    private static final String STEP_CONTENT_MARKER = "stepContent";

    private static FormService _formService = SpringContextService.getBean( FormService.BEAN_NAME );

    private List<ICompositeDisplay> _listChildren = new ArrayList<ICompositeDisplay>( );
    private List<ICompositeDisplay> _listICompositeDisplay = new ArrayList<ICompositeDisplay>( );
    private Step _step;
    private Map<Integer, List<Response>> _mapStepResponses = new HashMap<Integer, List<Response>>( );
	private List<Control> _listDisplayControls = new ArrayList<Control>();

    /**
     * Constructor
     * 
     * @param nIdStep
     *            the step identifier
     */
    public StepDisplayTree( int nIdStep )
    {
        initStepTree( nIdStep );
    }

    /**
     * Initialize the composite tree
     * 
     * @param nIdStep
     *            The step primary key
     */
    public void initStepTree( int nIdStep )
    {
        _step = StepHome.findByPrimaryKey( nIdStep );

        if ( _step != null )
        {
            List<FormDisplay> listStepFormDisplay = FormDisplayHome.getFormDisplayListByParent( nIdStep, 0 );
			_listDisplayControls = new ArrayList<Control>();
            for ( FormDisplay formDisplayChild : listStepFormDisplay )
            {
                ICompositeDisplay composite = _formService.formDisplayToComposite( formDisplayChild );
                _listChildren.add( composite );
                composite.initComposite( formDisplayChild );
				_listDisplayControls.addAll( composite.getAllDisplayControls() );
            }
        }
    }

    /**
     * Build and return the html template of the tree for Front-Office display
     * 
     * @param locale
     *            the locale
     * @param bIsForEdition
     *            The boolean which tell if the built Template is for edition or only for display value
     * @return the html template of the tree as a String
     */
    public String getCompositeHtml( Locale locale, boolean bIsForEdition )
    {
        Map<String, Object> model = new HashMap<String, Object>( );

        StringBuilder strBuilder = new StringBuilder( );

        for ( ICompositeDisplay child : _listChildren )
        {
            strBuilder.append( child.getCompositeHtml( locale, bIsForEdition ) );
        }

        model.put( STEP_TITLE_MARKER, _step.getTitle( ) );
        model.put( STEP_CONTENT_MARKER, strBuilder.toString( ) );

        String strTemplate = bIsForEdition ? STEP_TEMPLATE : STEP_TEMPLATE_READ_ONLY;

        return AppTemplateService.getTemplate( strTemplate, locale, model ).getHtml( );
    }

    /**
     * Build and return all the composite display of the tree as a flat List
     * 
     * @return the list of composite display
     */
    public List<ICompositeDisplay> getCompositeList( )
    {
        for ( ICompositeDisplay child : _listChildren )
        {
            _listICompositeDisplay.addAll( child.getCompositeList( ) );
        }
        return _listICompositeDisplay;
    }

    /**
     * @return the _step
     */
    public Step getStep( )
    {
        return _step;
    }

    /**
     * @param step
     *            the step to set
     */
    public void setStep( Step step )
    {
        this._step = step;
    }

    /**
     * 
     * @return mapStepResponses The map containing question responses and potential errors
     */
    public Map<Integer, List<Response>> getResponses( )
    {
        return _mapStepResponses;
    }

    /**
     * 
     * @param mapStepResponses
     *            The map containing question responses and potential errors
     */
    public void setResponses( Map<Integer, List<Response>> mapStepResponses )
    {
        _mapStepResponses = mapStepResponses;

        for ( ICompositeDisplay composite : _listChildren )
        {
            composite.setResponses( mapStepResponses );
        }
    }

	/**
	 * 
	 * @return the list of display controls for this display tree
	 */
	public List<Control> getListDisplayControls()
	{
		return _listDisplayControls;
	}
}
