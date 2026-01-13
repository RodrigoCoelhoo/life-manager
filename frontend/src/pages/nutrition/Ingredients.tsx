import { useEffect, useState } from "react";
import type { PageResponseDTO } from "../../services/api.dto";
import Loading from "../../components/common/Loading";
import ErrorMessage from "../../components/common/Error";
import { Pagination } from "../../components/common/Pagination";
import toast from "react-hot-toast";
import type { IngredientDetailsDTO, IngredientDTO } from "../../services/nutrition/ingredient/ingredient.dto";
import { ingredientService } from "../../services/nutrition/ingredient/ingredient.service";
import IngredientCard from "../../components/nutrition/IngredientCard";
import { Modal } from "../../components/common/Modal";
import IngredientForm from "../../components/nutrition/IngredientForm";
import type { IngredientBrandDetailsResponseDTO, IngredientBrandDTO } from "../../services/nutrition/ingredient-brand/ingredient-brand.dto";
import { ingredientBrandService } from "../../services/nutrition/ingredient-brand/ingredient-brand.service";
import { SearchBar } from "../../components/common/SearchBar";
import { GiTomato } from "react-icons/gi";

export default function Ingredients() {
	const [ingredients, setIngredients] = useState<IngredientDetailsDTO[]>([]);
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);

	const [search, setSearch] = useState<string>("");
	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const [elementsPerPage, setElementsPerPage] = useState<number>(18);

	const [createIngredientOpen, setCreateIngredientOpen] = useState<boolean>(false);

	const fetchIngredients = async () => {
		try {
			setError(null);
			const data: PageResponseDTO<IngredientDetailsDTO> = await ingredientService.getIngredients(page - 1, elementsPerPage, search);

			setIngredients(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch ingredients");
		} finally {
			setLoading(false);
		}
	};

	const createIngredient = async (ingredient: IngredientDTO) => {
		try {
			setLoading(true);
			const data: IngredientDetailsDTO = await ingredientService.createIngredient(ingredient);
			setIngredients(prev => {
				const updated = [data, ...prev];

				if (updated.length > elementsPerPage) {
					updated.pop();
				}

				return updated;
			});

			setTotalElements(prev => prev + 1);
		} catch (err) {
			console.error(err);
			toast.error("Failed to create ingredient");
		} finally {
			setLoading(false);
		}
	};

	const updateIngredient = async (id: number, updatedData: IngredientDTO) => {
		try {
			setLoading(true);
			const updated = await ingredientService.updateIngredient(id, updatedData);
			setIngredients(prev => prev.map(e => (e.id === id ? updated : e)));
		} catch (err) {
			toast.error("Failed to update ingredient");
		} finally {
			setLoading(false);
		}
	};

	const deleteIngredient = async (id: number) => {
		try {
			setLoading(true);
			await ingredientService.deleteIngredient(id);
			fetchIngredients();
			setTotalElements(prev => prev - 1);
		} catch (err) {
			toast.error("Failed to delete ingredient");
		} finally {
			setLoading(false);
		}
	};


	const createIngredientBrand = async (ingredientId: number, brand: IngredientBrandDTO) => {
		try {
			const newBrand: IngredientBrandDetailsResponseDTO = await ingredientBrandService.createIngredientBrand(ingredientId, brand);

			setIngredients(prev =>
				prev.map(ing => {
					if (ing.id === ingredientId) {
						return {
							...ing,
							brands: [...(ing.brands || []), newBrand],
						};
					}
					return ing;
				})
			);

		} catch (err) {
			console.error(err);
			toast.error("Failed to create ingredient brand");
		}
	};


	const updateIngredientBrand = async (ingredientId: number, brandId: number, updatedData: IngredientBrandDTO) => {
		try {
			const updatedBrand = await ingredientBrandService.updateIngredientBrand(ingredientId, brandId, updatedData);

			setIngredients(prev =>
				prev.map(ing => {
					if (ing.id === ingredientId) {
						return {
							...ing,
							brands: ing.brands?.map(b => (b.id === brandId ? updatedBrand : b)) || [],
						};
					}
					return ing;
				})
			);

		} catch (err) {
			console.error(err);
			toast.error("Failed to update ingredient brand");
		}
	};


	const deleteIngredientBrand = async (ingredientId: number, brandId: number) => {
		try {
			await ingredientBrandService.deleteIngredientBrand(ingredientId, brandId);

			setIngredients(prev =>
				prev.map(ing => {
					if (ing.id === ingredientId) {
						return {
							...ing,
							brands: ing.brands?.filter(b => b.id !== brandId) || [],
						};
					}
					return ing;
				})
			);

		} catch (err) {
			console.error(err);
			toast.error("Failed to delete ingredient brand");
		}
	};


	useEffect(() => {
		fetchIngredients();
	}, [page, elementsPerPage, search]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load exercises"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchIngredients()}
				/>
			</div>
		)
	};

	return (
		<>
			<div className="w-full p-6 text-textcolor flex flex-col gap-4">

				<div className="flex items-center justify-between gap-4">
					<button
						className="bg-primary p-2 px-4 rounded-xl cursor-pointer hover:bg-primary/80 font-semibold"
						onClick={() => setCreateIngredientOpen(true)}
					>
						Create +
					</button>

					<div className="flex items-center gap-3">
						<div className="flex gap-3 items-center">
							<label htmlFor="exercisesPerPage" className="text-sm mb-1 font-extralight">
								Ingredients per page
							</label>

							<select
								id="exercisesPerPage"
								name="exercisesPerPage"
								required
								className="form-input w-14"
								value={elementsPerPage}
								onChange={(e) => setElementsPerPage(Number(e.target.value))}
							>
								<option value="12">12</option>
								<option value="18">18</option>
								<option value="24">24</option>
							</select>
						</div>

						<span className="text-secondary"> {totalElements} {totalElements === 1 ? "ingredient" : "ingredients"} </span>
					</div>
				</div>

				<SearchBar
					value={search}
					onChange={setSearch}
					placeholder="Search ingredient by name..."
				/>

				<div>
					{ingredients.length === 0 ? (
						<div className="flex items-center justify-center min-h-[70vh]">
							<div className="flex flex-col items-center justify-center rounded-2xl p-8 text-center">
								<div className="mb-4 text-primary/70">
									<GiTomato size={32}/> 
								</div>

								<p className="text-xl font-medium text-textcolor mb-2">
									No ingredients yet
								</p>

								<p className="text-sm text-textcolor/60 max-w-md">
									Create your first ingredients to start tracking your meals
								</p>
							</div>
						</div>
					) : (
						<div className="flex flex-col gap-5">
							{ingredients.map((ingredient) => (
								<IngredientCard
									key={ingredient.id}
									{...ingredient}
									onUpdate={updateIngredient}
									onDelete={deleteIngredient}
									onCreateBrand={createIngredientBrand}
									onDeleteBrand={deleteIngredientBrand}
									onUpdateBrand={updateIngredientBrand}
								/>
							))}
						</div>
					)}
				</div>

				<div className="mb-4">
					<Pagination
						currentPage={page}
						totalPages={totalPages}
						onPageChange={(p: number) => setPage(p)}
					/>
				</div>
			</div>

			<Modal isOpen={createIngredientOpen} onClose={() => setCreateIngredientOpen(false)}>
				<IngredientForm
					onClose={() => setCreateIngredientOpen(false)}
					onCreate={createIngredient}
				/>
			</Modal>
		</>
	);
}
