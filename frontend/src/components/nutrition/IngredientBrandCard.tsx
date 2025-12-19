import { useState } from "react";
import { IoIosArrowForward } from "react-icons/io";
import { FaEdit } from "react-icons/fa";
import type { IngredientBrandDetailsResponseDTO, IngredientBrandDTO } from "../../services/nutrition/ingredient-brand/ingredient-brand.dto";
import React from "react";
import { Modal } from "../common/Modal";
import IngredientBrandForm from "./IngredientBrandForm";
//import { Modal } from "../common/Modal";

interface IngredientBrandCardProps extends IngredientBrandDetailsResponseDTO {
	ingredientId: number;
	onUpdate?: (ingredientId: number, brandId: number, updatedData: IngredientBrandDTO) => Promise<void>;
	onDelete?: (ingredientId: number, brandId: number) => Promise<void>;
}

export default function IngredientBrandCard({ onUpdate, onDelete, ingredientId, ...brand }: IngredientBrandCardProps) {
	const [isOpen, setIsOpen] = useState(false);
	const [updateIngredientBrandOpen, setUpdateIngredientBrandOpen] = useState<boolean>(false);

	return (
		<>
			<div className={`flex flex-col text-textcolor w-full ${isOpen ? "mb-5" : ""}`}>
				<div
					className={`flex flex-row justify-between items-center cursor-pointer ${isOpen ? "bg-secondary/20" : "bg-primary/20"} p-3 hover:bg-secondary/20 hover:text-secondary shadow-lg`}
					onClick={() => setIsOpen(!isOpen)}
				>
					<div className="flex flex-row gap-3 items-center">
						<h2 className="text-xl font-bold">{brand.name}</h2>
					</div>

					<div className="flex flex-row gap-3 text-textcolor items-center">
						<div>
							<FaEdit
								className="hover:text-secondary cursor-pointer"
								size={16}
								onClick={(e) => {
									e.stopPropagation();
									setUpdateIngredientBrandOpen(true);
								}}
							/>
						</div>
						<span className={`transition-transform ${isOpen ? "rotate-90" : ""}`}>
							<IoIosArrowForward />
						</span>
					</div>
				</div>

				{isOpen &&
					<div className="w-full h-full bg-background/90 px-10 py-2 grid grid-cols-[10fr_10fr] shadow-lg">
						<div className="font-bold text-left p-1">Nutrient</div>
						<div className="font-bold text-right p-1">per 100g</div>
						{brand.nutritionalValues.map((item, index) => {
							const rowStyle = index % 2 === 0 ? "bg-foreground/90 p-1" : "bg-foreground/30 p-1";

							return (
								<React.Fragment key={item.id}>
									<div className={`${rowStyle} text-left flex flex-row gap-3 font-extralight`}>
										{item.nutrient}
										<span className="text-sm text-textcolor/80">{item.unit}</span>
									</div>
									<div className={`${rowStyle} text-right font-extralight`}>{item.per100units.toFixed(2)}</div>
								</React.Fragment>
							);
						})}
					</div>
				}
			</div>

			<Modal isOpen={updateIngredientBrandOpen} onClose={() => setUpdateIngredientBrandOpen(false)}>
				<IngredientBrandForm
					onClose={() => setUpdateIngredientBrandOpen(false)}
					brand={brand}
					ingredientId={ingredientId}
					onUpdate={onUpdate}
					onDelete={onDelete}
				/>
			</Modal>
		</>
	);
}
