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
package fr.paris.lutece.plugins.forms.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for FormDisplay objects
 */
public final class FormDisplayHome
{
    // Static variable pointed at the DAO instance
    private static IFormDisplayDAO _dao = SpringContextService.getBean( "forms.formDisplayDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "forms" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private FormDisplayHome( )
    {
    }

    /**
     * Create an instance of the formDisplay class
     * 
     * @param formDisplay
     *            The instance of the FormDisplay which contains the informations to store
     * @return The instance of formDisplay which has been created with its primary key.
     */
    public static FormDisplay create( FormDisplay formDisplay )
    {
        _dao.insert( formDisplay, _plugin );

        return formDisplay;
    }

    /**
     * Update of the formDisplay which is specified in parameter
     * 
     * @param formDisplay
     *            The instance of the FormDisplay which contains the data to store
     * @return The instance of the formDisplay which has been updated
     */
    public static FormDisplay update( FormDisplay formDisplay )
    {
        _dao.store( formDisplay, _plugin );

        return formDisplay;
    }

    /**
     * Remove the formDisplay whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formDisplay Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a formDisplay whose identifier is specified in parameter
     * 
     * @param nKey
     *            The formDisplay primary key
     * @return an instance of FormDisplay
     */
    public static FormDisplay findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the formDisplay objects and returns them as a list
     * 
     * @return the list which contains the data of all the formDisplay objects
     */
    public static List<FormDisplay> getFormDisplayList( )
    {
        return _dao.selectFormDisplayList( _plugin );
    }

    /**
     * Load the data of all the formDisplay objects by parent and returns them as a list
     * 
     * @param nIdStep
     *            The step primary key
     * @param nIdParent
     *            The parent primary key
     * @return the list which contains the data of all the formDisplay objects by parent
     */
    public static List<FormDisplay> getFormDisplayListByParent( int nIdStep, int nIdParent )
    {
        return _dao.selectFormDisplayListByParent( nIdStep, nIdParent, _plugin );
    }

}