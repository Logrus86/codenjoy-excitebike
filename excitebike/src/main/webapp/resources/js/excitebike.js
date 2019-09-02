/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

window.onload = function(){
    let initTribune = function(canvasElement) {
        let tribuneDiv = document.createElement('div');
        tribuneDiv.id = 'tribune';
        tribuneDiv.style.height = canvasElement.height / 6 + 'px';
        canvasElement.parentNode.insertBefore(tribuneDiv, canvasElement);
    }
    
    let cutOffHalfOfCanvas = function(canvasElement) {
        var halfCanvasHeight = (canvasElement.height / 2).toFixed(0);
        canvasElement.style.bottom = halfCanvasHeight + 'px';

        var canvasWrapper = document.createElement('div');
        canvasWrapper.id = 'canvasWrapper';
        canvasWrapper.height = halfCanvasHeight;
        canvasWrapper.style.height = halfCanvasHeight + 'px';

        canvasElement.parentNode.insertBefore(canvasWrapper, canvasElement);
        canvasWrapper.appendChild(canvasElement);
    }
            
    let injectCssWithUiMods = function() {
        let divCanvas = document.getElementsByClassName('player-canvas');
        if(divCanvas!=null && document.getElementsByTagName('canvas').length>1){
            let canvasElement = document.getElementById(game.playerName);
            if(canvasElement==null){
                canvasElement = document.getElementsByTagName('canvas')[0];
            }
            document.getElementsByTagName("head")[0].insertAdjacentHTML(
                     "beforeend",
                     "<link rel=\"stylesheet\" href=\""+document.location.origin+game.contextPath+"/resources/css/"+game.gameName+".css"+"\" />");
            initTribune(canvasElement);
            cutOffHalfOfCanvas(canvasElement);
        } else {
            setTimeout(injectCssWithUiMods, 100);
        }
    }
    injectCssWithUiMods();
}