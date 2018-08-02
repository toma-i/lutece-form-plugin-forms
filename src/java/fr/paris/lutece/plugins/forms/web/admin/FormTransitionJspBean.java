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

package fr.paris.lutece.plugins.forms.web.admin;

import fr.paris.lutece.plugins.forms.business.Control;
import fr.paris.lutece.plugins.forms.business.ControlHome;
import fr.paris.lutece.plugins.forms.business.ControlType;
import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.Step;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.business.Transition;
import fr.paris.lutece.plugins.forms.business.TransitionHome;
import fr.paris.lutece.plugins.forms.service.EntryServiceManager;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.genericattributes.business.EntryType;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class provides the user interface to manage Form features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageTransitions.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MANAGEMENT" )
public class FormTransitionJspBean extends AbstractJspBean
{

    private static final long serialVersionUID = -9023450166890042022L;

    private static final String EMPTY_STRING = "";

    // Templates
    private static final String TEMPLATE_MANAGE_TRANSITIONS = "/admin/plugins/forms/manage_transitions.html";
    private static final String TEMPLATE_CREATE_TRANSITION = "/admin/plugins/forms/create_transition.html";
    private static final String TEMPLATE_MODIFY_TRANSITION = "/admin/plugins/forms/modify_transition.html";

    private static final String TEMPLATE_CREATE_CONTROL = "/admin/plugins/forms/create_control.html";
    private static final String TEMPLATE_MODIFY_CONTROL = "/admin/plugins/forms/modify_control.html";

    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MODIFY_TRANSITION = "forms.modify_transition.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_TRANSITION = "forms.create_transition.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_CONTROL = "forms.modify_control.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_CONTROL = "forms.create_control.pageTitle";

    // Markers
    private static final String MARK_TRANSITION_LIST = "transition_list";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_LOCALE = "locale";

    // Properties
    private static final String PROPERTY_ITEM_PER_PAGE = "forms.itemsPerPage";
    private static final String DEFAULT_TRANSITION_NO_CONTROL = "forms.manage_transitions.labelNoControl";

    // Validations
    private static final String TRANSITION_VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.transition.attribute.";
    private static final String CONTROL_VALIDATION_ATTRIBUTES_PREFIX = "forms.model.entity.control.attribute.";

    // Views
    private static final String VIEW_MANAGE_TRANSITIONS = "manageTransitions";
    private static final String VIEW_CREATE_TRANSITION = "createTransition";
    private static final String VIEW_MODIFY_TRANSITION = "modifyTransition";
    private static final String VIEW_CONFIRM_REMOVE_TRANSITION = "confirmRemoveTransition";

    private static final String VIEW_CREATE_CONTROL = "createControl";
    private static final String VIEW_MODIFY_CONTROL = "modifyControl";
    private static final String VIEW_CONFIRM_REMOVE_CONTROL = "confirmRemoveControl";

    // Actions
    private static final String ACTION_CREATE_TRANSITION = "createTransition";
    private static final String ACTION_CREATE_TRANSITION_AND_CONTROL = "createTransitionAndControl";
    private static final String ACTION_MODIFY_TRANSITION = "modifyTransition";
    private static final String ACTION_REMOVE_TRANSITION = "removeTransition";

    private static final String ACTION_CREATE_CONTROL = "createControl";
    private static final String ACTION_MODIFY_CONTROL = "modifyControl";
    private static final String ACTION_REMOVE_CONTROL = "removeControl";

    // Infos
    private static final String INFO_TRANSITION_CREATED = "forms.info.transition.created";
    private static final String INFO_TRANSITION_UPDATED = "forms.info.transition.updated";
    private static final String INFO_TRANSITION_REMOVED = "forms.info.transition.removed";
    private static final String MESSAGE_CONFIRM_REMOVE_TRANSITION = "forms.message.confirmRemoveTransition";

    private static final String INFO_CONTROL_CREATED = "forms.info.control.created";
    private static final String INFO_CONTROL_UPDATED = "forms.info.control.updated";
    private static final String INFO_CONTROL_REMOVED = "forms.info.control.removed";
    private static final String MESSAGE_CONFIRM_REMOVE_CONTROL = "forms.message.confirmRemoveControl";

    // Warning
    private static final String WARNING_NO_TRANSITION_STEP_FINAL = "forms.warning.transition.cannotAdd.finalStep";
    // Errors
    private static final String ERROR_TRANSITION_REMOVED = "forms.error.deleteTransition";
    private static final String ERROR_TRANSITION_EMPTY = "forms.error.emptyTransition";
    private static final String ERROR_CONTROL_REMOVED = "forms.error.deleteControl";

    private static final String ACTION_DO_MOVE_PRIORITY_UP = "moveUpPriority";

    private static final String ACTION_DO_MOVE_PRIORITY_DOWN = "moveDownPriority";

    // Session variable to store working values
    private Transition _transition;
    private Control _control;
    private Step _step;
    private Form _form;

    private final int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_TRANSITIONS, defaultView = true )
    public String getManageTransition( HttpServletRequest request )
    {
        _transition = null;
        _control = null;

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdStep == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectToViewManageForm( request );
        }

        _step = StepHome.findByPrimaryKey( nIdStep );

        if ( _step != null )
        {
            int nIdForm = _step.getIdForm( );

            if ( _form == null || _form.getId( ) != nIdForm )
            {
                _form = FormHome.findByPrimaryKey( nIdForm );
            }
        }

        Locale locale = getLocale( );

        if ( _step.isFinal( ) )
        {
            addWarning( WARNING_NO_TRANSITION_STEP_FINAL, locale );
        }

        Map<String, Object> model = getModel( );

        List<Transition> listTransition = TransitionHome.getTransitionsListFromStep( nIdStep );

        model.put( MARK_TRANSITION_LIST, listTransition );
        model.put( FormsConstants.MARK_STEP, _step );
        model.put( FormsConstants.MARK_FORM, _form );

        setPageTitleProperty( EMPTY_STRING );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_TRANSITIONS, locale, model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Returns the form to create a transition
     *
     * @param request
     *            The Http request
     * @return the html code of the transition form
     */
    @View( VIEW_CREATE_TRANSITION )
    public String getCreateTransition( HttpServletRequest request )
    {
        if ( !retrieveStepFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        _transition = ( _transition != null ) ? _transition : new Transition( );

        Map<String, Object> model = getModel( );

        if ( _step != null )
        {
            buildTransitionModel( _step, _transition, model );
        }
        else
        {
            redirectToViewManageForm( request );
        }

        return getPage( PROPERTY_PAGE_TITLE_CREATE_TRANSITION, TEMPLATE_CREATE_TRANSITION, model );
    }

    /**
     * Returns the form to modify a transition
     *
     * @param request
     *            The Http request
     * @return the html code of the transition form
     */
    @View( VIEW_MODIFY_TRANSITION )
    public String getModifyTransition( HttpServletRequest request )
    {

        if ( !retrieveStepFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( !retrieveTransitionFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        Map<String, Object> model = getModel( );

        if ( _transition != null && _step != null )
        {
            buildTransitionModel( _step, _transition, model );
        }
        else
        {
            redirectToViewManageForm( request );
        }

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_TRANSITION, TEMPLATE_MODIFY_TRANSITION, model );
    }

    /**
     * Returns the form to create a control
     *
     * @param request
     *            The Http request
     * @return the html code of the transition form
     */
    @View( VIEW_CREATE_CONTROL )
    public String getCreateControl( HttpServletRequest request )
    {
        if ( _transition == null || StringUtils.isEmpty( _transition.getNextStepTitle( ) ) )
        {
            int nIdTransition = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_TRANSITION ), FormsConstants.DEFAULT_ID_VALUE );

            if ( nIdTransition == FormsConstants.DEFAULT_ID_VALUE )
            {
                addError( ERROR_TRANSITION_EMPTY );
                return redirectToViewManageForm( request );
            }

            _transition = TransitionHome.findByPrimaryKey( nIdTransition );

            if ( _step == null || _transition.getFromStep( ) != _step.getId( ) )
            {
                _step = StepHome.findByPrimaryKey( _transition.getFromStep( ) );
            }
        }

        if ( _control == null )
        {
            _control = new Control( );
            _control.setControlType( ControlType.TRANSITION.getLabel( ) );
        }

        Map<String, Object> model = getModel( );

        if ( _step != null )
        {
            buildTransitionModel( _step, _transition, model );
        }
        else
        {
            redirectToViewManageForm( request );
        }

        if ( _transition != null )
        {
            int nIdQuestion = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_QUESTION ), FormsConstants.DEFAULT_ID_VALUE );
            _control.setIdQuestion( nIdQuestion );

            buildControlModel( nIdQuestion, model );
        }
        else
        {
            addError( ERROR_TRANSITION_EMPTY );
            redirectToViewManageForm( request );
        }

        return getPage( PROPERTY_PAGE_TITLE_CREATE_CONTROL, TEMPLATE_CREATE_CONTROL, model );
    }

    /**
     * Returns the form to modify a control
     *
     * @param request
     *            The Http request
     * @return the html code of the control form
     */
    @View( VIEW_MODIFY_CONTROL )
    public String getModifyControl( HttpServletRequest request )
    {
        if ( !retrieveStepFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( !retrieveTransitionFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        Map<String, Object> model = getModel( );

        if ( _transition != null && _step != null )
        {
            buildTransitionModel( _step, _transition, model );
        }
        else
        {
            redirectToViewManageForm( request );
        }

        if ( _transition != null )
        {
            if ( _control == null || _control.getId( ) != _transition.getIdControl( ) )
            {
                _control = ControlHome.findByPrimaryKey( _transition.getIdControl( ) );
            }

            int nIdQuestion = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_QUESTION ), FormsConstants.DEFAULT_ID_VALUE );

            if ( nIdQuestion != FormsConstants.DEFAULT_ID_VALUE )
            {
                _control.setIdQuestion( nIdQuestion );
            }

            buildControlModel( _control.getIdQuestion( ), model );
        }
        else
        {
            addError( ERROR_TRANSITION_EMPTY );
            redirectToViewManageForm( request );
        }

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_CONTROL, TEMPLATE_MODIFY_CONTROL, model );
    }

    /**
     * Build the model for Create and Modify Transition views
     * 
     * @param step
     *            the Step object
     * @param transition
     *            the Transition object
     * @param model
     *            the Model
     */
    private void buildTransitionModel( Step step, Transition transition, Map<String, Object> model )
    {
        ReferenceList listTransitionTargetSteps = getTransitionTargetStepReferenceList( _step.getIdForm( ), _step.getId( ) );

        ReferenceList listTransitionControls = new ReferenceList( );
        listTransitionControls.addItem( 0, I18nService.getLocalizedString( DEFAULT_TRANSITION_NO_CONTROL, getLocale( ) ) );
        // TODO: Retrieve a Control object list
        /*
         * listTransitionControls.addAll( ...);
         */
        model.put( FormsConstants.MARK_TRANSITION, _transition );
        model.put( FormsConstants.MARK_AVAILABLE_STEPS, listTransitionTargetSteps );
        model.put( FormsConstants.MARK_TRANSITION_CONTROL_LIST, listTransitionControls );

        model.put( FormsConstants.MARK_STEP, _step );

    }

    /**
     * Build the model for Create and Modify Control views
     * 
     * @param nIdQuestion
     *            the selected Question
     * @param model
     *            the Model
     */
    private void buildControlModel( int nIdQuestion, Map<String, Object> model )
    {
        if ( nIdQuestion != FormsConstants.DEFAULT_ID_VALUE )
        {
            Question question = QuestionHome.findByPrimaryKey( nIdQuestion );

            if ( question != null && question.getEntry( ) != null )
            {
                EntryType entryType = question.getEntry( ).getEntryType( );
                ReferenceList availableValidator = EntryServiceManager.getInstance( ).getAvailableValidator( entryType );

                model.put( FormsConstants.MARK_AVAILABLE_VALIDATORS, availableValidator );
            }
        }

        model.put( FormsConstants.MARK_CONTROL, _control );
        model.put( FormsConstants.MARK_QUESTION_LIST, QuestionHome.getQuestionsReferenceListByForm( _step.getIdForm( ) ) );
    }

    /**
     * Process the data capture of a new transition and redirect to transition management
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TRANSITION )
    public String doCreateTransition( HttpServletRequest request )
    {
        if ( !createTransition( request ) )
        {
            return redirect( request, VIEW_CREATE_TRANSITION, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
        }

        addInfo( INFO_TRANSITION_CREATED, getLocale( ) );

        return redirect( request, VIEW_MANAGE_TRANSITIONS, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
    }

    /**
     * Process the data capture of a new transition and redirect to control creation
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TRANSITION_AND_CONTROL )
    public String doCreateTransitionAndControl( HttpServletRequest request )
    {
        if ( !createTransition( request ) )
        {
            return redirect( request, VIEW_CREATE_TRANSITION, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
        }

        addInfo( INFO_TRANSITION_CREATED, getLocale( ) );

        return redirect( request, VIEW_CREATE_CONTROL, FormsConstants.PARAMETER_ID_TRANSITION, _transition.getId( ) );
    }

    /**
     * Process the data capture of a new transition
     *
     * @param request
     *            The Http Request
     * @return The boolean indicate the success or failure of the transition
     */
    private boolean createTransition( HttpServletRequest request )
    {
        if ( _transition == null )
        {
            _transition = new Transition( );
        }
        populate( _transition, request, getLocale( ) );

        if ( !validateTransition( _transition ) )
        {
            return false;
        }

        TransitionHome.create( _transition );

        return true;
    }

    /**
     * Process the data capture of a new transition
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_CONTROL )
    public String doCreateControl( HttpServletRequest request )
    {
        if ( _control == null )
        {
            _control = new Control( );
            _control.setControlType( ControlType.TRANSITION.getLabel( ) );
        }

        populate( _control, request, getLocale( ) );

        if ( !validateControl( _control ) )
        {
            return redirect( request, VIEW_CREATE_CONTROL, FormsConstants.PARAMETER_ID_TRANSITION, _transition.getId( ) );
        }

        ControlHome.create( _control );

        _transition.setIdControl( _control.getId( ) );
        TransitionHome.update( _transition );

        addInfo( INFO_CONTROL_CREATED, getLocale( ) );

        return redirect( request, VIEW_MANAGE_TRANSITIONS, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
    }

    /**
     * Process the data modification of a transition
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_TRANSITION )
    public String doModifyTransition( HttpServletRequest request )
    {
        if ( !retrieveTransitionFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        populate( _transition, request, getLocale( ) );

        if ( !validateTransition( _transition ) )
        {
            return redirect( request, VIEW_MODIFY_TRANSITION, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ),
                    FormsConstants.PARAMETER_ID_TRANSITION, _transition.getId( ) );
        }

        TransitionHome.update( _transition );

        addInfo( INFO_TRANSITION_UPDATED, getLocale( ) );

        return redirect( request, VIEW_MANAGE_TRANSITIONS, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
    }

    /**
     * Process the data modification of a control
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_MODIFY_CONTROL )
    public String doModifyControl( HttpServletRequest request )
    {
        if ( !retrieveTransitionFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        populate( _control, request, getLocale( ) );

        if ( !validateControl( _control ) )
        {
            return redirect( request, VIEW_MODIFY_CONTROL, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ),
                    FormsConstants.PARAMETER_ID_TRANSITION, _transition.getId( ) );
        }

        ControlHome.update( _control );

        addInfo( INFO_CONTROL_UPDATED, getLocale( ) );

        return redirect( request, VIEW_MANAGE_TRANSITIONS, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
    }

    /**
     * Manages the removal form of a Control whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( VIEW_CONFIRM_REMOVE_CONTROL )
    public String getConfirmRemoveControl( HttpServletRequest request )
    {
        int nIdTransitionToRemove = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_TRANSITION ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdTransitionToRemove == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectToViewManageForm( request );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_CONTROL ) );
        url.addParameter( FormsConstants.PARAMETER_ID_TRANSITION, nIdTransitionToRemove );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CONTROL, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );

    }

    /**
     * Handles the removal of a Transition
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage Transition
     */
    @Action( ACTION_REMOVE_CONTROL )
    public String doRemoveControl( HttpServletRequest request )
    {
        if ( !retrieveTransitionFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        int nIdTransitionToRemove = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_TRANSITION ), FormsConstants.DEFAULT_ID_VALUE );

        if ( _transition == null || _transition.getId( ) != nIdTransitionToRemove )
        {
            _transition = TransitionHome.findByPrimaryKey( nIdTransitionToRemove );
        }

        if ( _transition != null )
        {
            ControlHome.remove( _transition.getIdControl( ) );

            _transition.setIdControl( 0 );
            TransitionHome.update( _transition );

            addInfo( INFO_CONTROL_REMOVED, getLocale( ) );
        }
        else
        {
            addError( ERROR_CONTROL_REMOVED, getLocale( ) );
            return redirectToViewManageForm( request );
        }

        return redirect( request, VIEW_MANAGE_TRANSITIONS, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
    }

    /**
     * Manages the removal form of a Transition whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @View( VIEW_CONFIRM_REMOVE_TRANSITION )
    public String getConfirmRemoveTransition( HttpServletRequest request )
    {
        int nIdTransitionToRemove = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_TRANSITION ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdTransitionToRemove == FormsConstants.DEFAULT_ID_VALUE )
        {
            return redirectToViewManageForm( request );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_TRANSITION ) );
        url.addParameter( FormsConstants.PARAMETER_ID_TRANSITION, nIdTransitionToRemove );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_TRANSITION, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );

    }

    /**
     * Handles the removal of a Transition
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage Transition
     */
    @Action( ACTION_REMOVE_TRANSITION )
    public String doRemoveTransition( HttpServletRequest request )
    {
        if ( !retrieveTransitionFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( _transition != null )
        {
            TransitionHome.remove( _transition.getId( ) );

            if ( _transition.getIdControl( ) != 0 )
            {
                ControlHome.remove( _transition.getIdControl( ) );
            }

            TransitionHome.rebuildPrioritySequence( _transition.getFromStep( ) );

            addInfo( INFO_TRANSITION_REMOVED, getLocale( ) );
        }
        else
        {
            addError( ERROR_TRANSITION_REMOVED, getLocale( ) );
            return redirectToViewManageForm( request );
        }

        return redirect( request, VIEW_MANAGE_TRANSITIONS, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
    }

    /**
     * Move transition priority up
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_PRIORITY_UP )
    public String doMovePriorityUp( HttpServletRequest request )
    {
        return doMovePriority( request, true );
    }

    /**
     * Move transition priority down
     * 
     * @param request
     *            The request
     * @return The next URL to redirect to
     */
    @Action( ACTION_DO_MOVE_PRIORITY_DOWN )
    public String doMovePriorityDown( HttpServletRequest request )
    {
        return doMovePriority( request, false );
    }

    /**
     * Move transition priority up or down
     * 
     * @param request
     *            The request
     * @param bMoveUp
     *            True to move the priority up, false to move it down
     * @return The next URL to redirect to
     */
    private String doMovePriority( HttpServletRequest request, boolean bMoveUp )
    {
        if ( !retrieveStepFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        if ( !retrieveTransitionFromRequest( request ) )
        {
            return redirectToViewManageForm( request );
        }

        int nOldPriority = _transition.getPriority( );
        int nNewPriority = bMoveUp ? ( nOldPriority - 1 ) : ( nOldPriority + 1 );

        Transition transitionToInversePriority = TransitionHome.getTransitionByPriority( _step.getId( ), nNewPriority );

        if ( transitionToInversePriority != null )
        {
            transitionToInversePriority.setPriority( nOldPriority );
            TransitionHome.update( transitionToInversePriority );
        }
        _transition.setPriority( nNewPriority );
        TransitionHome.update( _transition );

        return redirect( request, VIEW_MANAGE_TRANSITIONS, FormsConstants.PARAMETER_ID_STEP, _transition.getFromStep( ) );
    }

    /**
     * Retrieve the step object from request parameter
     * 
     * @param request
     *            The request
     * 
     * @return false if an error occurred, true otherwise
     */
    private boolean retrieveStepFromRequest( HttpServletRequest request )
    {
        boolean bSuccess = true;

        int nIdStep = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_STEP ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdStep == FormsConstants.DEFAULT_ID_VALUE )
        {
            bSuccess = false;
        }

        if ( _step == null || _step.getId( ) != nIdStep )
        {
            _step = StepHome.findByPrimaryKey( nIdStep );
        }
        return bSuccess;
    }

    /**
     * Retrieve the transition object from request parameter
     * 
     * @param request
     *            The request
     * 
     * @return false if an error occurred, true otherwise
     */
    private boolean retrieveTransitionFromRequest( HttpServletRequest request )
    {
        boolean bSuccess = true;
        int nIdTransition = NumberUtils.toInt( request.getParameter( FormsConstants.PARAMETER_ID_TRANSITION ), FormsConstants.DEFAULT_ID_VALUE );

        if ( nIdTransition == FormsConstants.DEFAULT_ID_VALUE )
        {
            bSuccess = false;
        }

        if ( _transition == null || _transition.getId( ) != nIdTransition )
        {
            _transition = TransitionHome.findByPrimaryKey( nIdTransition );
        }
        return bSuccess;
    }

    /**
     * Validate the Transition field values
     * 
     * @param transition
     *            the Transition to validate
     * 
     * @return True if the transition is valid
     */
    private boolean validateTransition( Transition transition )
    {
        return validateBean( transition, TRANSITION_VALIDATION_ATTRIBUTES_PREFIX );
    }

    /**
     * Validate the Control field values
     * 
     * @param control
     *            the Control to validate
     * 
     * @return True if the control is valid
     */
    private boolean validateControl( Control control )
    {
        return validateBean( control, CONTROL_VALIDATION_ATTRIBUTES_PREFIX );
    }

    /**
     * Build a referenceList containing all the target Steps of a transition, for a given Form.
     * 
     * @param nIdForm
     *            the Form identifier
     * 
     * @param nIdStep
     *            the identifier of the transition origin step
     * 
     * @return a referenceList with all the target Steps
     */
    private ReferenceList getTransitionTargetStepReferenceList( int nIdForm, int nIdStep )
    {
        ReferenceList listTransitionTargetSteps = new ReferenceList( );

        for ( ReferenceItem step : StepHome.getStepReferenceListByForm( nIdForm ) )
        {
            if ( NumberUtils.toInt( step.getCode( ) ) != nIdStep )
            {
                listTransitionTargetSteps.add( step );
            }
        }
        return listTransitionTargetSteps;
    }

}