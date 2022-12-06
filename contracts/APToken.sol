//SPDX-License-Identifier: Unlicense
pragma solidity  ^0.8.0;

import "hardhat/console.sol";
import "@openzeppelin/contracts/token/ERC20/ERC20.sol";
import "@openzeppelin/contracts/token/ERC20/IERC20.sol";

contract APToken is ERC20 {

    address private _owner ;

    mapping(address => uint) pointpool;
    uint public _settingdistance;
    modifier onlyOwner() { // Modifier
        require(
            msg.sender == _owner,
            "Only onwer can call this."
        );
        _;
    }


    constructor(string memory tokenname,string memory symbol) ERC20(tokenname,symbol){
        // _mint(msg.sender,initialtotal);
        _owner = msg.sender;
        _settingdistance = 500;
        console.log("Deploying a Greeter with tokenname:", tokenname);
    }


    function AddSignalPoint(address point, uint distance) external {
        pointpool[point] = distance;
    }
    //信标点，信标点位置 
    function MintWithCondition(address user,uint amount,address point) external returns(bool){
        

        // pointpool[point] 
        // require(distance  <= pointpool[point],"not fit min distance requirements");

        //发放ap点
        _mint(user,amount);
        return true;
    }

}